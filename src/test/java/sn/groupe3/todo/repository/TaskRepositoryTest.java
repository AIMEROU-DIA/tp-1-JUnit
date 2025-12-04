package sn.groupe3.todo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import sn.groupe3.todo.model.Task;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void testSauvegarderTache() {
        Task tache = new Task("Tâche Test", "Description Test", false);
        Task tacheSauvegardee = taskRepository.save(tache);

        assertNotNull(tacheSauvegardee.getId());
        assertEquals("Tâche Test", tacheSauvegardee.getTitle());
    }

    @Test
    void testRecupererToutesLesTaches() {
        Task tache1 = new Task("Tâche 1", "Description 1", false);
        Task tache2 = new Task("Tâche 2", "Description 2", true);
        taskRepository.save(tache1);
        taskRepository.save(tache2);

        List<Task> taches = taskRepository.findAll();
        assertEquals(2, taches.size());
    }

    @Test
    void testRecupererTacheParId() {
        Task tache = new Task("Tâche à trouver", "Description", false);
        Task tacheSauvegardee = taskRepository.save(tache);
        Long idGenere = tacheSauvegardee.getId();

        Optional<Task> tacheTrouvee = taskRepository.findById(idGenere);
        assertTrue(tacheTrouvee.isPresent());
        assertEquals("Tâche à trouver", tacheTrouvee.get().getTitle());
    }

    @Test
    void testRecupererTacheParId_Inexistante() {
        Optional<Task> tacheTrouvee = taskRepository.findById(999L);
        assertFalse(tacheTrouvee.isPresent());
    }

    @Test
    void testSupprimerTacheParId() {
        Task tache = new Task("Tâche à supprimer", "Description", false);
        Task tacheSauvegardee = taskRepository.save(tache);
        Long idASupprimer = tacheSauvegardee.getId();

        taskRepository.deleteById(idASupprimer);

        Optional<Task> tacheSupprimee = taskRepository.findById(idASupprimer);
        assertFalse(tacheSupprimee.isPresent());
    }

    @Test
    void testSauvegarderTacheCompletee() {
        Task tacheCompletee = new Task("Tâche", "Description", true);
        Task resultat = taskRepository.save(tacheCompletee);

        assertTrue(resultat.isCompleted());
    }

    @Test
    void testSauvegarderTacheAvecDescriptionLongue() {
        String longueDescription =
            "Ceci est une description très longue pour tester " +
            "la persistance des données dans la base de données H2 " +
            "utilisée pour les tests.";

        Task tache = new Task("Tâche avec longue description",
                              longueDescription,
                              false);

        Task resultat = taskRepository.save(tache);

        assertEquals(longueDescription, resultat.getDescription());
    }
}
