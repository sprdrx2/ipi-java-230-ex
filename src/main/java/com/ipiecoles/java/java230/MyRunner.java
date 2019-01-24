package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.exceptions.BatchException;
import com.ipiecoles.java.java230.model.Commercial;
import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.model.Manager;
import com.ipiecoles.java.java230.model.Technicien;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;
import com.ipiecoles.java.java230.repository.TechnicienRepository;
import org.hibernate.engine.jdbc.batch.spi.Batch;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MyRunner implements CommandLineRunner {

    private static final String REGEX_MATRICULE = "^[MTC][0-9]{5}$";
    private static final String REGEX_NOM = ".*";
    private static final String REGEX_PRENOM = ".*";
    private static final int NB_CHAMPS_MANAGER = 5;
    private static final int NB_CHAMPS_TECHNICIEN = 7;
    private static final String REGEX_MATRICULE_MANAGER = "^M[0-9]{5}$";
    private static final int NB_CHAMPS_COMMERCIAL = 7;
    private int lineNo;


    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private List<Employe> employes = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        String fileName = "employes.csv";
        readFile(fileName);
        //readFile(strings[0]);
    }

    /**
     * Méthode qui lit le fichier CSV en paramètre afin d'intégrer son contenu en BDD
     * @param fileName Le nom du fichier (à mettre dans src/main/resources)
     * @return une liste contenant les employés à insérer en BDD ou null si le fichier n'a pas pu être le
     */
    public List<Employe> readFile(String fileName) throws Exception {
        Stream<String> stream;
        try {
            stream = Files.lines(Paths.get(new ClassPathResource(fileName).getURI()));
        }
        catch (FileNotFoundException e) {
            logger.error("le fichier " + fileName + "n'existe pas." );
            return null;
        }

        logger.info(""); logger.info("STARTING BATCH COBOL STYLE"); logger.info("");

        List<String> lignes = stream.collect(Collectors.toList());
        lineNo = 0;
        lignes.forEach(ligne -> {
            try {
                lineNo++;
                processLine(ligne);
            } catch (BatchException e) {
                ligneProblematique(lineNo, ligne, e.getMessage());
            }
        });

        return employes;
    }

    /**
     * Méthode qui regarde le premier caractère de la ligne et appelle la bonne méthode de création d'employé
     * @param ligne la ligne à analyser
     * @throws BatchException si le type d'employé n'a pas été reconnu
     */
    private void processLine(String ligne) throws BatchException {
        if(ligne.startsWith("C")) {
            try {
                this.processCommercial(ligne);
            }
            catch(Exception e) {
                throw new BatchException(e.getMessage());
            }
        }
        else if (ligne.startsWith("M")) {
            try {
                this.processManager(ligne);
            }
            catch(Exception e) {
                throw new BatchException(e.getMessage());
            }
        }
        else if (ligne.startsWith("T")) {
            try {
                this.processTechnicien(ligne);
            }
            catch(Exception e) {
                throw new BatchException(e.getMessage());
            }
        }
        else { throw new BatchException("type matricule invalide."); };
    }

    /**
     * Méthode qui crée un Commercial à partir d'une ligne contenant les informations d'un commercial et l'ajoute dans la liste globale des employés
     * @param ligneCommercial la ligne contenant les infos du commercial à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processCommercial(String ligneCommercial) throws BatchException {
        Commercial c = new Commercial();

        //logger.info("traitement commercial: " + ligneCommercial);
        String[] l = ligneCommercial.split(",");
        if(l.length != NB_CHAMPS_COMMERCIAL) {
            throw new BatchException("nombre de champs incorrect.");
        }

       c = (Commercial) processCommon(ligneCommercial, (Employe) c);

       Double caAnnuel;
       try {
            caAnnuel = Double.parseDouble(l[5]);
        }
        catch (Exception e){
            throw new BatchException("champ caAnnuel invalide: " + e.getMessage());
        }

        Integer performance;
        try {
            performance = Integer.parseInt(l[6]);
        }
        catch (Exception e) {
            throw new BatchException("champ performance invalide: " + e.getMessage());
        }

        try {
            c.setCaAnnuel(caAnnuel);
            c.setPerformance(performance);
        }
        catch (Exception e) {
            throw new BatchException("creation objet impossible: " + e.getMessage());
        }
        saveToBdd(c);
        logger.info("OK: " + c.toString());
    }

    /**
     * Méthode qui crée un Manager à partir d'une ligne contenant les informations d'un manager et l'ajoute dans la liste globale des employés
     * @param ligneManager la ligne contenant les infos du manager à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processManager(String ligneManager) throws BatchException {
        Manager m = new Manager();

        //logger.info("traitement manager: " + ligneManager);
        String[] l = ligneManager.split(",");
        if(l.length != NB_CHAMPS_MANAGER) {
            throw new BatchException("nombre de champs incorrect.");
        }

        m = (Manager) processCommon(ligneManager, (Employe) m);


        try {
            HashSet<Technicien> equipe = new HashSet();
            m.setEquipe(equipe);
        }
        catch (Exception e) {
            throw new BatchException("creation objet impossible: " + e.getMessage());
        }

        saveToBdd(m);
        logger.info("OK: " + m.toString());
    }

    /**
     * Méthode qui crée un Technicien à partir d'une ligne contenant les informations d'un technicien et l'ajoute dans la liste globale des employés
     * @param ligneTechnicien la ligne contenant les infos du technicien à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processTechnicien(String ligneTechnicien) throws BatchException {
        Technicien t = new Technicien();

        //logger.info("traitement technicien: " + ligneTechnicien);
        String[] l = ligneTechnicien.split(",");
        if(l.length != NB_CHAMPS_TECHNICIEN) {
            throw new BatchException("nombre de champs incorrect.");
        }

        t = (Technicien) processCommon(ligneTechnicien, (Employe) t);


        Integer grade;
        try {
            grade = Integer.parseInt(l[5]);
        }
        catch(Exception e) {
            throw new BatchException("champ grade invalide: " + e.getMessage());
        }

        String manager = l[6];
        if(!manager.matches(REGEX_MATRICULE_MANAGER)) {
            throw new BatchException("champ manager invalide. ");
        }
        if(managerRepository.findByMatricule(manager) == null) {
            throw new BatchException("manager " + manager + " inconnu.");
        }

        try {
            t.setGrade(grade);
            t.setManager(managerRepository.findByMatricule(manager));
            t.setSalaire(Double.parseDouble(l[4]));
        }
        catch (Exception e) {
            throw new BatchException("creation objet impossible: " + e.getMessage());
        }

        saveToBdd(t);
        logger.info("OK: " + t.toString());
    }

    private void ligneProblematique(Integer lineNo, String ligne, String msg) {
        logger.error("ligne " + lineNo.toString() + ": " + msg + " \t\t| " + ligne);
    }

    private void saveToBdd(Employe emp) throws BatchException {
        if (employeRepository.findByMatricule(emp.getMatricule()) != null) {
            throw new BatchException("matricule " + emp.getMatricule() + " déjà utilisé.");
        }

        try {
            employeRepository.save(emp);
        }
        catch (Exception e) {
            throw new BatchException("insertion impossible: " + e.getMessage());
        }
    }

    private Employe processCommon(String ligne, Employe emp) throws BatchException {
        String[] l = ligne.split(",");

        String matricule, nom, prenom;
        matricule = l[0]; nom = l[1]; prenom = l[2];
        if(!matricule.matches(REGEX_MATRICULE)) {
            throw new BatchException("format matricule invalide.");
        }

        LocalDate dateEmbauche;
        try {
            dateEmbauche = DateTimeFormat.forPattern("dd/mm/YYYY").parseLocalDate(l[3]);
        }
        catch (Exception e) {
            throw new BatchException("date invalide: " + e.getMessage());
        }

        Double salaire;
        try {
            salaire = Double.parseDouble(l[4]);
        }
        catch(Exception e) {
            throw new BatchException("champ salaire invalide: " + e.getMessage());
        }

        try {
            emp.setMatricule(matricule);
            emp.setNom(nom);
            emp.setPrenom(prenom);
            emp.setDateEmbauche(dateEmbauche);
            // les techniciens ont besoin du grade pour calculer le salaire
            // pour cette classe, le champ salaire est donc délégué à processTechnicien
            if(!(emp instanceof Technicien)) {
                emp.setSalaire(salaire);
            }
        }
        catch(Exception e) {
            throw new BatchException("creation objet impossible: " + e.getMessage());
        }

        return emp;
    }

}
