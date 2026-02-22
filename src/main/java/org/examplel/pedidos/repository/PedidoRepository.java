package org.examplel.pedidos.repository;

import org.examplel.pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteUsername(String username);
}
