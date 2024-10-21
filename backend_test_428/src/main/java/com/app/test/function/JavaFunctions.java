package com.app.test.function;

import com.app.test.model.Manager;
import com.app.test.model.Pet;
import com.app.test.model.PetCareCenter;
import com.app.test.model.PetOwner;
import com.app.test.model.Document;
import com.app.test.model.PetService;
import com.app.test.enums.PetServiceType;
import com.app.test.converter.PetServiceTypeConverter;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmFunction;
import com.sap.olingo.jpa.metadata.core.edm.annotation.EdmParameter;
import com.sap.olingo.jpa.metadata.core.edm.mapper.extension.ODataFunction;
import com.app.test.repository.PetServiceRepository;
import com.app.test.repository.PetOwnerRepository;
import com.app.test.repository.PetCareCenterRepository;
import com.app.test.repository.ManagerRepository;
import com.app.test.repository.DocumentRepository;
import com.app.test.repository.PetRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

@Component
public class JavaFunctions implements ODataFunction {


    
    
}
   
