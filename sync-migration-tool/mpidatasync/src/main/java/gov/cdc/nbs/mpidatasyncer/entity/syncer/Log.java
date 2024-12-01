package gov.cdc.nbs.mpidatasyncer.entity.syncer;

import gov.cdc.nbs.mpidatasyncer.enums.LogLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "logs")
@Data
@NoArgsConstructor
public class Log {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Timestamp timestamp;
  private String level;
  private String message;


  public Log(LogLevel logLevel,String message) {
    this.timestamp=new Timestamp(System.currentTimeMillis());
    this.level =logLevel.getStatus();
    this.message = message;
  }

}

