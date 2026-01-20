package com.example.proyecto.domain.enums;

import java.util.Arrays;
import java.util.List;

public enum Categorias {
    LIMPIEZA,
    PLOMERIA,
    ELECTRICISTA,
    CARPINTERIA,
    PINTURA,
    JARDINERIA,
    CUIDADOS,
    CONSTRUCCION,
    ABRAZADERAS,
    ACCESORIOS_HIDRAULICOS,
    ACUMULADORES_HIDRAULICOS,
    BOMBAS_HIDRAULICAS,
    DIAGTRONICS,
    ENFRIADORES_HIDRAULICOS,
    FILTROS_HIDRAULICOS,
    MOTORES_HIDRAULICOS,
    PRESOSTATOS,
    RADIO_CONTROL,
    TUBERIA_HIDRAULICA_SIN_SOLDADURA,
    VALVULAS_HIDRAULICAS;
    public static List<Categorias> listarCategorias() {
        return Arrays.asList(Categorias.values());
    }
}