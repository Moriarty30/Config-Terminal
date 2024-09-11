package com.superpay.config.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity(name = "terminals_configs")
public class TerminalConfigEntity {
    @Id
    @Column(name = "id")
    @Builder.Default private String id = UUID.randomUUID().toString();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "terminal_id", nullable = false)
    public TerminalEntity terminalEntity;
    @Column(name = "code")
    private String code;
    @Column(name = "type")
    private String type;
    @Column(name = "value")
    private String value;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "created_at_tz")
    private LocalDateTime createdAtTz;
    @Column(name = "tag")
    private String tag;
}
