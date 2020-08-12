package com.oocl.parkingreservationservice.controller;

import com.oocl.parkingreservationservice.constants.MessageConstants;
import com.oocl.parkingreservationservice.constants.StatusContants;
import com.oocl.parkingreservationservice.handler.GlobalExceptionHandler;
import com.oocl.parkingreservationservice.model.ParkingOrder;
import com.oocl.parkingreservationservice.repository.ParkingOrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ParkingOrderIntegrationTest {
    private MockMvc mockMvc;
    @Autowired
    private ParkingOrderRepository parkingOrderRepository;
    @Autowired
    private ParkingOrderController parkingOrderController;
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void before() {
        mockMvc = MockMvcBuilders.standaloneSetup(parkingOrderController, globalExceptionHandler).build();
    }

    @AfterEach
    public void after() {
        parkingOrderRepository.deleteAll();
        assert parkingOrderRepository.findAll().isEmpty();
    }

    @Test
    void should_return_confirm_order_when_confirm_a_order_given_order_id() throws Exception {
//        given
        Integer orderId = 1;
        ParkingOrder parkingOrder = new ParkingOrder(orderId, 1L, "2020-8-10 12:25:30",
                "2020-8-10 14:25:30", 1, 1, "2020-8-10 14:25:30", StatusContants.WAIT_FOR_SURE, "1234", 10.0);
        ParkingOrder savedParkingOrder = parkingOrderRepository.save(parkingOrder);
//        when then
        mockMvc.perform(patch("/parkingOrders/" + savedParkingOrder.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("type", "comfirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(StatusContants.ALREADY_SURE));
    }

    @Test
    void should_return_illegal_message_when_confirm_a_confirmed_order_given_order_id() throws Exception {
//        given
        Integer orderId = 1;
        ParkingOrder parkingOrder = new ParkingOrder(orderId, 1L, "2020-8-10 12:25:30",
                "2020-8-10 14:25:30", 1, 1, "2020-8-10 14:25:30", StatusContants.ALREADY_SURE, "1234", 10.0);
        ParkingOrder savedParkingOrder = parkingOrderRepository.save(parkingOrder);
//        when then
        mockMvc.perform(patch("/parkingOrders/" + savedParkingOrder.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("type", "comfirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.ODER_CONFIRMED));
    }

    @Test
    void should_return_illegal_message_when_confirm_a_canceled_order_given_order_id() throws Exception {
//        given
        Integer orderId = 1;
        ParkingOrder parkingOrder = new ParkingOrder(orderId, 1L, "2020-8-10 12:25:30",
                "2020-8-10 14:25:30", 1, 1, "2020-8-10 14:25:30", StatusContants.DELETED, "1234", 10.0);
        ParkingOrder savedParkingOrder = parkingOrderRepository.save(parkingOrder);
//        when then
        mockMvc.perform(patch("/parkingOrders/" + savedParkingOrder.getId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("type", "comfirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.ODER_CANCELED));
    }

    @Test
    void should_return_not_exist_message_when_confirm_a_not_exist_order_given_order_id() throws Exception {
//        given
        int orderId = Integer.MAX_VALUE;
//        when then
        mockMvc.perform(patch("/parkingOrders/" + orderId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("type", "comfirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(MessageConstants.ODER_NOT_EXIST));
    }
}
