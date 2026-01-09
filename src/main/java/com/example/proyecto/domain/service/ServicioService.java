package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.*;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.dto.FiltroServicioDTO;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {
}
