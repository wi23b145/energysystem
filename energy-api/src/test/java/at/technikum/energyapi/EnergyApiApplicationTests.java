package at.technikum.energyapi;

import at.technikum.energyapi.model.EnergyRecord;
import at.technikum.energyapi.service.EnergyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import at.technikum.energyapi.repository.EnergyRecordRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class EnergyApiApplicationTests {

	@Test
	public void testSave() {

		//Testdaten

		EnergyRecord record = new EnergyRecord();
		record.setCommunityName("Wien");
		record.setConsumptionKw(100.0);
		record.setProductionKw(120.0);
		record.setTimestamp("2025-06-19T21:59");

		//Mock-Repository
		EnergyRecordRepository mockRepo = mock(EnergyRecordRepository.class);
		when(mockRepo.save(record)).thenReturn(record);

		//Service manuell erzeugen

		EnergyService service = new EnergyService(mockRepo);

		//Testmethode aufrufen
		EnergyRecord result = service.save(record);


		//Ergebnis speichern
		assertEquals("Wien", result.getCommunityName());
		verify(mockRepo).save(record);

	}

}
