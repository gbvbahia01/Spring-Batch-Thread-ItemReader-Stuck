package br.com.gbvbahia.threads.monitor.rest;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.jayway.jsonpath.JsonPath;
import br.com.gbvbahia.threads.monitor.dto.ProcessStatus;
import br.com.gbvbahia.threads.monitor.dto.ProcessorDTO;
import br.com.gbvbahia.threads.monitor.repository.ProcessorRepository;
import br.com.gbvbahia.threads.monitor.until.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class ProcessorControllerTest {

  private Faker faker = Faker.instance();
  
  @Autowired
  private MockMvc mockMvc;

  //private ProcessorService processorService;
  @Autowired
  private ProcessorRepository processorRepository;
  
  private ObjectMapper objectMapper = new ObjectMapper();
  
  @BeforeEach
  void setUp() throws Exception {
    processorRepository.deleteAll();
  }

  @Test
  void testAdd() throws Exception {
    ProcessorDTO dto = ProcessorDTO.builder()
        .dataToProcess(faker.crypto().sha512())
        .name(faker.company().name())
        .urlToCall(faker.internet().url())
        .build();
    
    String path = String.format("%s%s", Constants.Rest.PATH_V1, Constants.Rest.PROCESSOR);
    
    MockHttpServletResponse response = mockMvc.perform(post(path)
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(toJson(dto)))
        .andDo(print())
        .andExpect(jsonPath("$.*", hasSize(7)))
        .andExpect(jsonPath("$.id", greaterThan(0)))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists())
        .andExpect(jsonPath("$.name").value(dto.getName()))
        .andExpect(jsonPath("$.dataToProcess").value(dto.getDataToProcess()))
        .andExpect(jsonPath("$.urlToCall").value(dto.getUrlToCall()))
        .andExpect(jsonPath("$.processStatus").value(ProcessStatus.WAITING.name()))
        .andExpect(status().isCreated()).andReturn().getResponse();
    
    Integer id = JsonPath.parse(response.getContentAsString()).read("$.id");
    Assertions.assertTrue(processorRepository.findById(id.longValue()).isPresent());
    
  }
  
  private String toJson(Object obj) throws Exception {
    return objectMapper.writeValueAsString(obj);
  }

}
