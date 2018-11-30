package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;
import com.ipiecoles.java.java230.repository.TechnicienRepository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.imageio.ImageTranscoder;
import java.util.List;

@Component
public class MyRunner implements CommandLineRunner {

    @Autowired
    private ManagerRepository mR;
    @Autowired
    private TechnicienRepository tR;

    @Override
    public void run(String... strings) throws Exception {

        System.out.println(tR.findByMatricule("T02141").getManager());
        mR.findWithEquipeByMatricule("M01517").getEquipe().forEach(MyRunner::print);
    }


    public static void print(Object t) {
        System.out.println(t);
    }
}
