package cl.duocuc.hardwarenexus.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boletas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_boleta", nullable = false, unique = true, length = 50)
    private String numeroBoleta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "fecha_emision", updatable = false)
    private LocalDateTime fechaEmision;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal iva;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String estado = "COMPLETADO";

    @Builder.Default
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleBoleta> detalles = new ArrayList<>();
}
