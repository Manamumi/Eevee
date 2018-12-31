package xyz.eevee.eevee.session;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BuildInfo {
    private String ciCommitSha;
    private String ciCommitMessage;
    private String ciJobId;
    private String builtBy;
    private String builtByName;
    private String builtById;
    private String buildTime;
}
