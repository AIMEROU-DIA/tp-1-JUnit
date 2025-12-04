package sn.groupe3.todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import sn.groupe3.todo.exception.ResourceNotFoundException;
import sn.groupe3.todo.model.Task;
import sn.groupe3.todo.service.TaskService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void testObtenirToutesLesTaches() throws Exception {
        Task tache1 = new Task("Tâche 1", "Description 1", false);
        tache1.setId(1L);

        Task tache2 = new Task("Tâche 2", "Description 2", true);
        tache2.setId(2L);

        when(taskService.getAllTasks()).thenReturn(Arrays.asList(tache1, tache2));

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Tâche 1"))
            .andExpect(jsonPath("$[1].title").value("Tâche 2"));
    }

    @Test
    void testObtenirTacheParId() throws Exception {
        Task tache = new Task("Tâche Test", "Description Test", false);
        tache.setId(1L);

        when(taskService.getTaskById(1L)).thenReturn(tache);

        mockMvc.perform(get("/api/tasks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Tâche Test"));
    }

    @Test
    void testCreerUneTache() throws Exception {
        Task tache = new Task("Nouvelle Tâche", "Nouvelle Description", false);
        tache.setId(1L);

        when(taskService.createTask(any(Task.class))).thenReturn(tache);

        String jsonTache = """
            {
                "title": "Nouvelle Tâche",
                "description": "Nouvelle Description",
                "completed": false
            }
            """;

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonTache))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Nouvelle Tâche"));
    }

    @Test
    void testModifierTache() throws Exception {
        Task tacheModifiee = new Task("Tâche Modifiée", "Description Modifiée", true);
        tacheModifiee.setId(1L);

        when(taskService.updateTask(eq(1L), any(Task.class))).thenReturn(tacheModifiee);

        String jsonModification = """
            {
                "title": "Tâche Modifiée",
                "description": "Description Modifiée",
                "completed": true
            }
            """;

        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonModification))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Tâche Modifiée"))
            .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void testSupprimerTache() throws Exception {
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
            .andExpect(status().isOk());
    }

    @Test
    void testCreerTache_SansCorpsDeRequete() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void testModifierTache_AvecIdInvalide() throws Exception {
        when(taskService.updateTask(eq(999L), any(Task.class)))
            .thenThrow(new ResourceNotFoundException("Tâche non trouvée"));

        String jsonModification = """
            {
                "title": "Tâche",
                "description": "Description",
                "completed": false
            }
            """;

        mockMvc.perform(put("/api/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonModification))
            .andExpect(status().isNotFound());
    }
}
