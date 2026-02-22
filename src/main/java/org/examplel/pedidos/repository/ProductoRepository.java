package org.examplel.pedidos.repository;

import org.examplel.pedidos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
