package com.example.dealership.vehicle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import com.example.dealership.error.ConflictException;
import com.example.dealership.error.NotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    private static final String VIN = "WVWZZZ1KZAW000001";
    private static final String VALID_BODY = """
            {
              "vin": "WVWZZZ1KZAW000001",
              "make": "Volkswagen",
              "model": "Golf",
              "productionYear": 2025,
              "plannedHandoverDate": "2026-12-15"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    void findAllReturnsVehicles() throws Exception {
        when(vehicleService.findAll()).thenReturn(List.of(response()));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].vin").value(VIN))
                .andExpect(jsonPath("$[0].plannedHandoverDate").value("2026-12-15"));
    }

    @Test
    void findByIdReturnsVehicle() throws Exception {
        when(vehicleService.findById(1L)).thenReturn(response());

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin").value(VIN));
    }

    @Test
    void findByIdReturnsNotFoundProblemWhenVehicleDoesNotExist() throws Exception {
        when(vehicleService.findById(99L)).thenThrow(new NotFoundException("Vehicle with id 99 not found"));

        mockMvc.perform(get("/api/vehicles/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("Vehicle with id 99 not found"));
    }

    @Test
    void createReturnsCreatedVehicle() throws Exception {
        when(vehicleService.create(any(VehicleRequest.class))).thenReturn(response());

        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/vehicles/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createReturnsBadRequestProblemWithFieldErrorsWhenRequestIsInvalid() throws Exception {
        String body = """
                {
                  "vin": "TOO-SHORT",
                  "make": "",
                  "model": "Golf",
                  "productionYear": %d
                }
                """.formatted(Year.now().getValue() + 2);

        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Validation failed"))
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors[0].field").value("make"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"))
                .andExpect(jsonPath("$.errors[1].field").value("productionYear"))
                .andExpect(jsonPath("$.errors[1].message").value("must be between 1990 and next year"))
                .andExpect(jsonPath("$.errors[2].field").value("vin"));
        verifyNoInteractions(vehicleService);
    }

    @Test
    void createReturnsConflictProblemWhenVinAlreadyExists() throws Exception {
        when(vehicleService.create(any(VehicleRequest.class)))
                .thenThrow(new ConflictException("Vehicle with VIN " + VIN + " already exists"));

        mockMvc.perform(post("/api/vehicles").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value("Vehicle with VIN " + VIN + " already exists"));
    }

    @Test
    void updateReturnsUpdatedVehicle() throws Exception {
        when(vehicleService.update(eq(1L), any(VehicleRequest.class))).thenReturn(response());

        mockMvc.perform(put("/api/vehicles/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vin").value(VIN));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService).delete(1L);
    }

    @Test
    void deleteReturnsNotFoundProblemWhenVehicleDoesNotExist() throws Exception {
        doThrow(new NotFoundException("Vehicle with id 99 not found")).when(vehicleService).delete(99L);

        mockMvc.perform(delete("/api/vehicles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private static VehicleResponse response() {
        return new VehicleResponse(1L, VIN, "Volkswagen", "Golf", 2025, LocalDate.of(2026, 12, 15));
    }
}
