package de.arbeitsagentur.opdt.keycloak.filestore;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import de.arbeitsagentur.opdt.keycloak.filestore.client.FileClientEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.clientscope.FileClientScopeEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.AbstractEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.common.UpdatableEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.group.FileGroupEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.realm.FileRealmEntity;
import de.arbeitsagentur.opdt.keycloak.filestore.role.FileRoleEntity;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.env.EnvScalarConstructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Map.entry;
import static org.yaml.snakeyaml.env.EnvScalarConstructor.ENV_FORMAT;

public class EntityIO {
    public static final String ID_COMPONENT_SEPARATOR = ":";
    public static final Pattern ID_COMPONENT_SEPARATOR_PATTERN = Pattern.compile(Pattern.quote(ID_COMPONENT_SEPARATOR) + "+");
    private static final Pattern RESERVED_CHARACTERS = Pattern.compile("[%<:>\"/\\\\|?*=]");
    private static final String STORAGE_CONTEXT = "mapStorage";
    private static final String STORAGE_TYPE = "file";
    private static final String ESCAPING_CHARACTER = "=";
    public static final String FILE_SUFFIX = ".yaml";
    private static final Logger LOG = Logger.getLogger(EntityIO.class);

    static final Map<Class<? extends AbstractEntity>, Function<? extends AbstractEntity, String[]>> UNIQUE_HUMAN_READABLE_NAME_FIELD = Map.ofEntries(
            entry(FileRealmEntity.class, ((Function<FileRealmEntity, String[]>) v -> new String[]{v.getName()})),
            entry(FileClientEntity.class, ((Function<FileClientEntity, String[]>) v -> new String[]{v.getClientId()})),
            entry(FileClientScopeEntity.class, ((Function<FileClientScopeEntity, String[]>) v -> new String[]{v.getName()})),
            entry(FileGroupEntity.class, ((Function<FileGroupEntity, String[]>) v -> v.getParentId() == null
                    ? new String[]{v.getName()}
                    : new String[]{v.getParentId(), v.getName()})),
            entry(FileRoleEntity.class, ((Function<FileRoleEntity, String[]>) (v -> v.getClientId() == null
                    ? new String[]{v.getName()}
                    : new String[]{v.getClientId(), v.getName()})))
    );


    static <E extends AbstractEntity & UpdatableEntity> E yamlParseFile(Path fileName, Class<E> interfaceOfEntity) {
        var loaderoptions = new LoaderOptions();
        loaderoptions.setTagInspector(tag -> false);

        Constructor constructor = new KeycloakEnvScalarConstructor(new TypeDescription(interfaceOfEntity), null, loaderoptions);

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setIndicatorIndent(2);
        options.setIndentWithIndicator(false);

        Representer representer = new Representer(options);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        representer.getPropertyUtils().setBeanAccess(BeanAccess.FIELD); // Avoid circular dependencies when using setters

        Yaml yaml = new Yaml(constructor, representer);
        yaml.addImplicitResolver(EnvScalarConstructor.ENV_TAG, ENV_FORMAT, "$");

        try (InputStream inputStream = new FileInputStream(fileName.toString())) {
            return yaml.load(inputStream);
        } catch (Exception e) {
            LOG.error("Error with reading file: " + fileName.toString(), e);
            return null;
        }
    }

    static <E extends AbstractEntity & UpdatableEntity> void writeToFile(E entity, Path path) throws IOException {
        var loaderoptions = new LoaderOptions();
        loaderoptions.setTagInspector(tag -> false);
        Constructor constructor = new Constructor(entity.getClass(), loaderoptions);

        DumperOptions options = new DumperOptions();
        options.setIndent(4);
        options.setIndicatorIndent(2);
        options.setIndentWithIndicator(false);

        Representer representer = new Representer(options);
        representer.getPropertyUtils().setSkipMissingProperties(true);
        representer.addClassTag(Set.class, Tag.SEQ);

        Yaml yaml = new Yaml(constructor, representer);
        String output = yaml.dumpAs(entity, Tag.MAP, DumperOptions.FlowStyle.BLOCK);

        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        Files.write(path, output.getBytes());
    }

    static <E extends AbstractEntity & UpdatableEntity> E parseFile(Path fileName, Class<E> interfaceOfEntity) {
        final E parsedObject = yamlParseFile(fileName, interfaceOfEntity);
        if (parsedObject == null) {
            return null;
        }

        final String fileNameStr = fileName.getFileName().toString();
        final String idFromFilename = fileNameStr.substring(0, fileNameStr.length() - FILE_SUFFIX.length());
        String escapedId = determineKeyFromValue(parsedObject, interfaceOfEntity, idFromFilename);

        if (escapedId == null) {
            LOG.tracef("Determined ID from filename: %s%s", idFromFilename);
            escapedId = idFromFilename;
        } else if (!escapedId.endsWith(idFromFilename)) {
            LOG.warnf("Id \"%s\" does not conform with filename \"%s\", expected: %s", escapedId, fileNameStr, escapeId(escapedId));
        }

        if (parsedObject.getId() == null) {
            parsedObject.setId(escapedId);
        }

        parsedObject.clearUpdatedFlag();
        return parsedObject;
    }

    private static <E extends AbstractEntity & UpdatableEntity> String determineKeyFromValue(E value, Class<E> interfaceOfEntity, String lastIdComponentIfUnset) {
        String[] proposedId = getSuggestedPath(value, interfaceOfEntity);
        if (proposedId == null || proposedId.length == 0) {
            return lastIdComponentIfUnset;
        } else if (proposedId[proposedId.length - 1] == null) {
            proposedId[proposedId.length - 1] = lastIdComponentIfUnset;
        }

        String[] escapedProposedId = escapeId(proposedId);
        final String res = String.join(ID_COMPONENT_SEPARATOR, escapedProposedId);

        if (LOG.isTraceEnabled()) {
            LOG.tracef("determineKeyFromValue: got %s (%s) for %s", res, res == null ? null : String.join(" [/] ", proposedId), value);
        }

        return res;
    }

    private static <E extends AbstractEntity & UpdatableEntity> String[] getSuggestedPath(E entity, Class<E> clazz) {
        Function<E, String[]> fileNameByEntity = ((Function<E, String[]>) UNIQUE_HUMAN_READABLE_NAME_FIELD.get(clazz));
        if (fileNameByEntity == null) {
            fileNameByEntity = e -> e.getId() == null ? null : new String[]{e.getId()};
        }

        return fileNameByEntity.apply(entity);
    }

    public static String[] escapeId(String[] idArray) {
        if (idArray == null || idArray.length == 0 || idArray.length == 1 && idArray[0] == null) {
            return null;
        }

        return Stream.of(idArray)
                .map(EntityIO::escapeId)
                .toArray(String[]::new);
    }

    public static String escapeId(String id) {
        Objects.requireNonNull(id, "ID must be non-null");
        StringBuilder idEscaped = new StringBuilder();
        Matcher m = RESERVED_CHARACTERS.matcher(id);

        while (m.find()) {
            m.appendReplacement(idEscaped, String.format(ESCAPING_CHARACTER + "%02x", (int) m.group().charAt(0)));
        }

        m.appendTail(idEscaped);
        final Path pId = Path.of(idEscaped.toString());
        return pId.toString();
    }

    public static Path getPathForIdAndParentPath(String escapedId, Path parentPath) {
        String[] escapedIdArray = ID_COMPONENT_SEPARATOR_PATTERN.split(escapedId);
        Path parentDirectory = parentPath;
        Path targetPath = parentDirectory;

        for (String path : escapedIdArray) {
            targetPath = targetPath.resolve(path).normalize();
            if (!targetPath.getParent().equals(parentDirectory)) {
                LOG.warnf("Path traversal detected: %s", Arrays.toString(escapedIdArray));
                return null;
            }
            parentDirectory = targetPath;
        }

        return targetPath.resolveSibling(targetPath.getFileName() + FILE_SUFFIX);
    }

    public static Path getRootDirectory() {
        String[] scopes = {STORAGE_CONTEXT, STORAGE_TYPE};
        String root = Config.scope(scopes).get("dir");

        if (root == null) {
            String scopesString = String.join(",", scopes);
            throw new IllegalStateException("Map Storage file directory not found. This indicates that the environment variable is not set for this scope combination: " + scopesString);
        }

        return Path.of(root);
    }


    public static boolean canParseFile(Path p) {
        if (p == null) {
            return false;
        }

        final String fn = p.getFileName().toString();
        try {
            return Files.isRegularFile(p)
                    && Files.size(p) > 0L
                    && !fn.startsWith(".")
                    && fn.endsWith(FILE_SUFFIX)
                    && Files.isReadable(p);
        } catch (IOException ex) {
            return false;
        }
    }

    public static void deleteParentDirectoryIfEmpty(Path directory) throws IOException {
        Path parentDir = directory.getParent();
        while (parentDir != null && isDirectoryEmpty(parentDir)) {
            Files.delete(parentDir);
            parentDir = parentDir.getParent();
        }
    }

    public static boolean isDirectoryEmpty(Path directory) throws IOException {
        boolean isEmpty = false;
        if (Files.isDirectory(directory)) {
            try (Stream<Path> entries = Files.list(directory)) {
                isEmpty = entries.findFirst().isEmpty();
            }
        }

        return isEmpty;
    }
}
