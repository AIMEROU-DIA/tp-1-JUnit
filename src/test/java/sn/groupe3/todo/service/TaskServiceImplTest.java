package sn.groupe3.todo.service;

// Import des annotations JUnit 5
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// Import des annotations Mockito
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Import des classes du projet
import sn.groupe3.todo.model.Task;
import sn.groupe3.todo.repository.TaskRepository;

// Import des classes utilitaires Java
import java.util.Arrays;
import java.util.List;

// Import statique : permet d'utiliser les méthodes sans préfixer avec le nom de la classe
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
 
	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskServiceImpl taskService;

	@Test
	void testRecupererToutesLesTaches() {




		Task tache1 = new Task("Tache 1", "Description de la tache 1", false);
		Task tache2 = new Task("Tache 2", "Description de la tache 2", true);

		List<Task> taches = Arrays.asList(tache1, tache2);


		when(taskRepository.findAll()).thenReturn(taches);

		List<Task> tachesObtenues = taskService.getAllTasks() ;

		assertEquals(2, tachesObtenues.size());

		verify(taskRepository, times(1)).findAll();
	
	}


	@Test
	void testCreerTask(){
		Task tacheAcreer = new Task("Nouvelle Tache", "Description de la nouvelle tache", false);
		Task tacheSauvegardee = new Task("Nouvelle Tache", "Description de la nouvelle tache", false);
		tacheSauvegardee.setId(1L);

		when(taskRepository.save(tacheAcreer)).thenReturn(tacheSauvegardee);

		Task tacheCreee = taskService.createTask(tacheAcreer);

		assertNotNull(tacheCreee.getId());
		assertEquals("Nouvelle Tache", tacheCreee.getTitle());
		verify(taskRepository, times(1)).save(tacheAcreer);

	}

}
