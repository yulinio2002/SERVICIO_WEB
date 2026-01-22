package com.example.proyecto.config.seeders;

import com.example.proyecto.domain.entity.Empresa;
import com.example.proyecto.infrastructure.EmpresaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(19)
@Slf4j
public class DefaultEmpresaSeeder implements ApplicationRunner {

    private final EmpresaRepository empresaRepository;

    @Value("${DEFAULT_EMPRESA_NOMBRE:Oleohidraulicos S.A.C.}")
    private String nombre;

    @Value("${DEFAULT_EMPRESA_NOSOTROS:Oleohidraulics Services S.A.C. es una empresa con más de 14 años de experiencia en el mercado, especializada en mantenimiento, fabricación y comercialización de equipos y sistemas oleo-hidráulicos.\n" +
            "\n" +
            "Asimismo, contamos con servicios de mecanizado de precisión (CNC), área de diseño y proyectos especializados para planificar la mejor solución a sus problemas hidráulicos.}")
    private String nosotros;

    @Value("${DEFAULT_EMPRESA_MISION:Brindar soluciones integrales de ingeniería en oleo hidráulica, electrohidráulica,\n" +
            "mecánica industrial y automatización, mediante el suministro de componentes, la\n" +
            "prestación de servicios de mantenimiento y la fabricación de unidades de potencia\n" +
            "especializadas para la industria la minería y pesca, garantizando calidad, eficiencia\n" +
            "operativa y confiabilidad en cada proyecto.}")
    private String mision;

    @Value("${DEFAULT_EMPRESA_VISION:Ser una empresa líder y referente a nivel nacional en soluciones, oleo hidráulicas e\n" +
            "industriales, reconocida por su innovación, excelencia técnica y compromiso con el\n" +
            "desarrollo sostenible de nuestros clientes y aliados estratégicos.}")
    private String vision;

    @Value("${DEFAULT_EMPRESA_DIRECCION:Av. Ejemplo 123}")
    private String direccion;

    @Value("${DEFAULT_EMPRESA_RUC:99999999999}")
    private String ruc;

    @Value("${DEFAULT_EMPRESA_CONTACTO:987654321}")
    private String numeroContacto;

    @Value("${DEFAULT_EMPRESA_URL1:https://example.com}")
    private String url1;

    @Value("${DEFAULT_EMPRESA_URL2:https://example.com/contact}")
    private String url2;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            if (empresaRepository.existsByNombreIgnoreCase(nombre)) {
                log.info("Seeder Empresa: ya existe una empresa con nombre='{}', omitiendo creación.", nombre);
                return;
            }

            Empresa empresa = new Empresa();
            empresa.setNombre(nombre);
            empresa.setNosotros(nosotros);
            empresa.setMision(mision);
            empresa.setVision(vision);
            empresa.setDireccion(direccion);
            empresa.setRuc(ruc);
            empresa.setNumeroContacto(numeroContacto);
            empresa.setUrl1(url1);
            empresa.setUrl2(url2);

            empresa = empresaRepository.save(empresa);

            log.info("Seeder Empresa: empresa por defecto creada: {}", empresa.getNombre());
        } catch (Exception e) {
            log.error("Seeder Empresa: fallo al crear empresa por defecto: {}", e.getMessage(), e);
        }
    }
}
