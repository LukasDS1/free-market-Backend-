package com.freemarket.state_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.freemarket.state_service.model.state;
import com.freemarket.state_service.repository.stateRepository;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {
      @Mock
    private stateRepository stateRepository;
 
    @InjectMocks
    private stateService stateService;
 
    @Test
    void isAvailable_stateActivo_returnsActivo() {
        state s = new state();
        s.setStateName("ACTIVO");
 
        when(stateRepository.findById(1L)).thenReturn(Optional.of(s));
 
        String result = stateService.isAvailable(1L);
 
        assertThat(result).isEqualTo("ACTIVO");
    }
 
    @Test
    void isAvailable_stateInactivo_returnsInactivo() {
        state s = new state();
        s.setStateName("INACTIVO");
 
        when(stateRepository.findById(2L)).thenReturn(Optional.of(s));
 
        String result = stateService.isAvailable(2L);
 
        assertThat(result).isEqualTo("INACTIVO");
    }
 
    @Test
    void isAvailable_stateNotFound_throwsException() {
        when(stateRepository.findById(99L)).thenReturn(Optional.empty());
 
        assertThatThrownBy(() -> stateService.isAvailable(99L))
                .isInstanceOf(Exception.class);
    }

}
