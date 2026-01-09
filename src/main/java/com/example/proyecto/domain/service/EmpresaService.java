package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Empresa;
import com.example.proyecto.infrastructure.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    // Crear empresa
    public Empresa crearEmpresa(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    // Obtener todas las empresas
    public List<Empresa> listarEmpresas() {
        return empresaRepository.findAll();
    }

    // Obtener empresa por ID
    public Empresa obtenerEmpresaPorId(Long id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con ID: " + id));
    }

    // Actualizar SOLO los campos editables
    public Empresa actualizarEmpresa(Long id, Empresa empresaActualizada) {
        Empresa empresa = obtenerEmpresaPorId(id);

        empresa.setNombre(empresaActualizada.getNombre());
        empresa.setNosotros(empresaActualizada.getNosotros());
        empresa.setMision(empresaActualizada.getMision());
        empresa.setVision(empresaActualizada.getVision());
        empresa.setDireccion(empresaActualizada.getDireccion());
        empresa.setNumeroContacto(empresaActualizada.getNumeroContacto());
        empresa.setUrl1(empresaActualizada.getUrl1());
        empresa.setUrl2(empresaActualizada.getUrl2());

        // ruc NO se actualiza
        return empresaRepository.save(empresa);
    }

    // Eliminar empresa
    public void eliminarEmpresa(Long id) {
        if (!empresaRepository.existsById(id)) {
            throw new EntityNotFoundException("Empresa no encontrada con ID: " + id);
        }
        empresaRepository.deleteById(id);
    }

}
