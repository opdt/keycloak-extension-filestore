package de.arbeitsagentur.opdt.keycloak.filestore.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.keycloak.models.ProtocolMapperModel;

public class FileProtocolMapperUtils {

  private final String protocol;
  private static final ConcurrentMap<String, FileProtocolMapperUtils> INSTANCES =
      new ConcurrentHashMap<>();

  private FileProtocolMapperUtils(String protocol) {
    this.protocol = protocol;
  }

  public static FileProtocolMapperUtils instanceFor(String protocol) {
    Objects.requireNonNull(protocol);
    return INSTANCES.computeIfAbsent(protocol, FileProtocolMapperUtils::new);
  }

  public static FileProtocolMapperEntity fromModel(ProtocolMapperModel model) {
    FileProtocolMapperEntity res = new FileProtocolMapperEntity();
    res.setId(model.getId());
    res.setName(model.getName());
    res.setProtocolMapper(model.getProtocolMapper());
    res.setConfig(model.getConfig());
    return res;
  }

  public ProtocolMapperModel toModel(FileProtocolMapperEntity entity) {
    ProtocolMapperModel res = new ProtocolMapperModel();
    res.setId(entity.getId());
    res.setName(entity.getName());
    res.setProtocolMapper(entity.getProtocolMapper());
    Map<String, String> config = entity.getConfig();
    res.setConfig(config == null ? new HashMap<>() : new HashMap<>(config));
    res.setProtocol(protocol);
    return res;
  }
}
