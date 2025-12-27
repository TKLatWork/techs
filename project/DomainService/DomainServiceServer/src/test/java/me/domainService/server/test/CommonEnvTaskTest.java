package me.domainService.server.test;


import lombok.extern.slf4j.Slf4j;
import me.domainService.server.common.model.Env;
import me.domainService.server.common.service.EnvService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class CommonEnvTaskTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    EnvService envService;


    @Test
    void bookAndRunTest() throws Exception {
        // 初始状态应为 Idle
        Env envBefore = envService.get("DEV");
        assertEquals(Env.STATUS_IDLE, envBefore.getStatus());

        // book env for user ADMIN on DEV
        mockMvc.perform(get("/envTask/bookEnv/ADMIN/DEV"))
                .andExpect(status().isOk());

        // 断言状态变为 Booked
        Env envAfterBook = envService.get("DEV");
        assertEquals(Env.STATUS_BOOKED, envAfterBook.getStatus());

        // run task TASK1 on DEV
        mockMvc.perform(get("/envTask/runTask/TASK1/DEV"))
                .andExpect(status().isOk());

        // 断言状态变为 Running
        Env envAfterRun = envService.get("DEV");
        assertEquals(1, envAfterRun.getWorks().stream().filter(s -> {
            return s.contains("TASK1");
        }).count());
    }
}