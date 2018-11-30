package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.imageio.ImageTranscoder;
import java.util.List;

@Component
public class MyRunner implements CommandLineRunner {

    @Autowired
    private EmployeRepository employeRepository;

    @Override
    public void run(String... strings) throws Exception {
        Iterable<Employe> employes = employeRepository.findAll();
        employes.forEach(MyRunner::print);
        /*for(employes: employe) {
            System.out.println(e);
        }*/

        //Employe arale = new Employe("Arale","Norimaki","ATAT66", "21-12-1985", 100000000000.00 );

    }



    public static void print(Object t) {
        System.out.println(t);
    }
}
