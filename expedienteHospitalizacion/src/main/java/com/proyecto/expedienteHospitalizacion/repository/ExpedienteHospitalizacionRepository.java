import com.proyecto.expedienteHospitalizacion.repository.ExpedienteHospitalizacionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.expedienteHospitalizacion.model.ExpedienteHospitalizacionModel;

@Repository
public interface ExpedienteHospitalizacionRepository extends JpaRepository<ExpedienteHospitalizacionModel, Long> {

    List<ExpedienteHospitalizacionModel> listAllByRutPaciente(int rutPaciente);

    Optional<ExpedienteHospitalizacionModel> findByCodExpediente(String codExpediente);

    Optional<ExpedienteHospitalizacionModel> findByIdBooking(long idBooking);


}

