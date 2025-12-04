package sn.groupe3.todo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sn.groupe3.todo.exception.ResourceNotFoundException;
import sn.groupe3.todo.model.Task;
import sn.groupe3.todo.repository.TaskRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void testRecupererToutesLesTaches() {
        Task tache1 = new Task("Tâche 1", "Description 1", false);
        Task tache2 = new Task("Tâche 2", "Description 2", true);
        List<Task> tachesAttendues = Arrays.asList(tache1, tache2);

        when(taskRepository.findAll()).thenReturn(tachesAttendues);

        List<Task> tachesObtenues = taskService.getAllTasks();

        assertEquals(2, tachesObtenues.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testRecupererTacheParId_Succes() {
        Long idTache = 1L;
        Task tacheAttendue = new Task("Tâche", "Description", false);
        tacheAttendue.setId(idTache);

        when(taskRepository.findById(idTache))
                .thenReturn(Optional.of(tacheAttendue));

        Task tacheObtenue = taskService.getTaskById(idTache);

        assertEquals(idTache, tacheObtenue.getId());
        assertEquals("Tâche", tacheObtenue.getTitle());
    }

    @Test
    void testRecupererTacheParId_NonTrouvee() {
        Long idTache = 99L;
        when(taskRepository.findById(idTache))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(idTache);
        });
    }

    @Test
    void testCreerTache() {
        Task tacheACreer = new Task("Nouvelle Tâche", "Nouvelle Description", false);
        Task tacheSauvegardee = new Task("Nouvelle Tâche", "Nouvelle Description", false);
        tacheSauvegardee.setId(1L);

        when(taskRepository.save(tacheACreer)).thenReturn(tacheSauvegardee);

        Task resultat = taskService.createTask(tacheACreer);

        assertNotNull(resultat.getId());
        assertEquals("Nouvelle Tâche", resultat.getTitle());
        verify(taskRepository, times(1)).save(tacheACreer);
    }

    @Test
    void testCreerTache_Nulle() {
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(null);
        });
    }

    @Test
    void testModifierTache() {
        Long idTache = 1L;
        Task tacheExistante = new Task("Ancien Titre", "Ancienne Description", false);
        tacheExistante.setId(idTache);

        Task tacheModifiee = new Task("Nouveau Titre", "Nouvelle Description", true);

        when(taskRepository.findById(idTache))
                .thenReturn(Optional.of(tacheExistante));
        when(taskRepository.save(tacheExistante))
                .thenReturn(tacheExistante);

        Task resultat = taskService.updateTask(idTache, tacheModifiee);

        assertEquals("Nouveau Titre", resultat.getTitle());
        assertEquals("Nouvelle Description", resultat.getDescription());
        assertTrue(resultat.isCompleted());
        verify(taskRepository).findById(idTache);
        verify(taskRepository).save(tacheExistante);
    }

    @Test
    void testSupprimerTache() {
        Long idTache = 1L;
        when(taskRepository.existsById(idTache)).thenReturn(true);

        taskService.deleteTask(idTache);

        verify(taskRepository, times(1)).deleteById(idTache);
    }

    @Test
    void testSupprimerTache_NonTrouvee() {
        Long idTache = 99L;
        when(taskRepository.existsById(idTache)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(idTache);
        });
        
        verify(taskRepository, never()).deleteById(idTache);
    }

    @Test
    void testRecupererTacheParId_TacheInexistante() {
        Long idInexistant = 999L;
        when(taskRepository.findById(idInexistant))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            taskService.getTaskById(idInexistant);
        });

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void testCreerTache_AvecTitreVide() {
        Task tacheTitreVide = new Task("", "Description", false);
        Task tacheSauvegardee = new Task("", "Description", false);
        tacheSauvegardee.setId(1L);

        when(taskRepository.save(tacheTitreVide)).thenReturn(tacheSauvegardee);

        Task resultat = taskService.createTask(tacheTitreVide);

        assertNotNull(resultat);
        assertEquals("", resultat.getTitle());
        assertFalse(resultat.isCompleted());
        verify(taskRepository, times(1)).save(tacheTitreVide);
    }

    @Test
    void testModifierTache_TacheCompletee() {
        Long idTache = 1L;
        Task tacheExistante = new Task("Tâche", "Description", false);
        tacheExistante.setId(idTache);

        Task tacheModifiee = new Task("Tâche Modifiée", "Nouvelle Description", true);

        when(taskRepository.findById(idTache))
                .thenReturn(Optional.of(tacheExistante));
        when(taskRepository.save(tacheExistante))
                .thenReturn(tacheExistante);

        Task resultat = taskService.updateTask(idTache, tacheModifiee);

        assertTrue(resultat.isCompleted());
        assertEquals("Tâche Modifiée", resultat.getTitle());
        assertEquals("Nouvelle Description", resultat.getDescription());
        verify(taskRepository).findById(idTache);
        verify(taskRepository).save(tacheExistante);
    }

    @Test
    void testCreerTache_AvecDescriptionNull() {
        Task tacheDescriptionNull = new Task("Tâche", null, false);
        Task tacheSauvegardee = new Task("Tâche", null, false);
        tacheSauvegardee.setId(1L);

        when(taskRepository.save(tacheDescriptionNull)).thenReturn(tacheSauvegardee);

        Task resultat = taskService.createTask(tacheDescriptionNull);

        assertNotNull(resultat);
        assertEquals("Tâche", resultat.getTitle());
        assertNull(resultat.getDescription());
        assertFalse(resultat.isCompleted());
        verify(taskRepository, times(1)).save(tacheDescriptionNull);
    }

    @Test
    void testCreerTache_TacheDejaCompletee() {
        Task tacheCompletee = new Task("Tâche", "Description", true);
        Task tacheSauvegardee = new Task("Tâche", "Description", true);
        tacheSauvegardee.setId(1L);

        when(taskRepository.save(tacheCompletee)).thenReturn(tacheSauvegardee);

        Task resultat = taskService.createTask(tacheCompletee);

        assertTrue(resultat.isCompleted());
        verify(taskRepository, times(1)).save(tacheCompletee);
    }

    @Test
    void testModifierTache_Partiellement() {
        Long idTache = 1L;
        Task tacheExistante = new Task("Ancien Titre", "Ancienne Description", false);
        tacheExistante.setId(idTache);

        Task tacheModifiee = new Task("Nouveau Titre", null, false);

        when(taskRepository.findById(idTache))
                .thenReturn(Optional.of(tacheExistante));
        when(taskRepository.save(tacheExistante))
                .thenReturn(tacheExistante);

        Task resultat = taskService.updateTask(idTache, tacheModifiee);

        assertEquals("Nouveau Titre", resultat.getTitle());
        assertEquals("Ancienne Description", resultat.getDescription());
        assertFalse(resultat.isCompleted());
        verify(taskRepository).findById(idTache);
        verify(taskRepository).save(tacheExistante);
    }
}