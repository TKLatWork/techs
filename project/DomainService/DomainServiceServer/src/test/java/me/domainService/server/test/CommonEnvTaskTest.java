package me.domainService.server.test;


import lombok.extern.slf4j.Slf4j;
import me.domainService.application.entity.Env;
import me.domainService.application.repo.EnvRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    EnvRepo envRepo;


    @Test
    void bookAndRunTest() throws Exception {
        // 初始状态应为 Idle
        Env envBefore = envRepo.get("DEV");
        assertEquals(Env.STATUS_IDLE, envBefore.getStatus());


        // run task TASK1 on DEV
        mockMvc.perform(get("/envTask/runTask/ADMIN/TASK1/DEV"))
                .andExpect(status().isOk());

        // assert work log contains entry for TASK1
        Env envAfterRun = envRepo.get("DEV");
        assertEquals(1, envAfterRun.getWorks().stream().filter(s -> {
            return s.contains("TASK1");
        }).count());
    }
}