package org.examplel.pedidos.config;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VendedorOnly {
}
