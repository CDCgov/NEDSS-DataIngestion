package gov.cdc.nbs.mpidatasyncer.entity.syncer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "sync_metadata")
@Data
@NoArgsConstructor
public class SyncMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "last_sync_time", nullable = false)
  private LocalDateTime lastSyncTime;

  public SyncMetadata(LocalDateTime lastSyncTime) {
    this.lastSyncTime = lastSyncTime;
  }
}
