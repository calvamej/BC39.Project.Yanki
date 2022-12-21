package com.bootcamp.project.yanki.service;

import com.bootcamp.project.yanki.entity.BootcoinOperationDTO;
import com.bootcamp.project.yanki.entity.YankiDTO;
import com.bootcamp.project.yanki.entity.YankiEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Consumer {

	@Autowired
	private MongoTemplate mongoTemplate;

	public static final String topic = "mytopic";

	@Autowired
	private KafkaTemplate<String, YankiDTO> kafkaTemp;

	@KafkaListener(topics="BootcoinOperationTopic", groupId="mygroup")
	public void consumeFromTopic(BootcoinOperationDTO operationDTO) {
		try
		{
			YankiEntity entityPetitioner = new YankiEntity();
			YankiEntity entitySeller = new YankiEntity();
			String mensaje = "";
			if(operationDTO.getPaymentMethod().toUpperCase().equals("YANKI"))
			{
				entityPetitioner = getAccount(operationDTO.getPetitionerDocumentNumber());
				entitySeller = getAccount(operationDTO.getSellerDocumentNumber());
				if(entityPetitioner == null || entitySeller == null)
				{
					mensaje = "Petitioner or seller does not have a YANKI account.";
					System.out.println(mensaje);
				}
				else
				{
					publishToTopic(entityPetitioner.getDebitCardNumber(),"SEND", operationDTO.getAmount());
					publishToTopic(entitySeller.getDebitCardNumber(),"RECEIVE", operationDTO.getAmount());
				}
			}
			else
			{
				mensaje = "Bootcoin operation received, but not a YANKI one.";
				System.out.println(mensaje);
			}
		}
		catch (Exception ex)
		{

		}
	}
	public YankiEntity getAccount(String documentNumber)
	{
		Query query = new Query();
		query.addCriteria(Criteria.where("documentNumber").is(documentNumber));
		return mongoTemplate.findOne(query, YankiEntity.class);
	}
	public void publishToTopic(String debitCardNumber, String type, Double amount) {
		YankiDTO yankiDTO = new YankiDTO();
		yankiDTO.setDebitCardNumber(debitCardNumber);
		yankiDTO.setType(type);
		yankiDTO.setAmount(amount);
		this.kafkaTemp.send(topic, yankiDTO);
	}
}
