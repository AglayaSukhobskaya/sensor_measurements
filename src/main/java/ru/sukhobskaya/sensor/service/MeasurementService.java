package ru.sukhobskaya.sensor.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.sukhobskaya.sensor.dto.MeasurementDto;
import ru.sukhobskaya.sensor.model.Measurement;
import ru.sukhobskaya.sensor.repository.MeasurementRepository;
import ru.sukhobskaya.sensor.util.SensorValidator;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MeasurementService {
    MeasurementRepository measurementRepository;
    SensorService sensorService;
    SensorValidator sensorValidator;
    ModelMapper modelMapper;

    public List<MeasurementDto> findAll() {
        var measurements = measurementRepository.findAll();
        return measurements.stream()
                .map(measurement -> modelMapper.map(measurement, MeasurementDto.class))
                .toList();
    }

    public void create(Double temperature, Boolean isRainy, String sensorName) {
        var sensor = sensorService.findByName(sensorName);
        sensorValidator.validateSensorExist(sensorName, sensor);
        var measurement = Measurement.builder()
                .temperature(temperature)
                .isRainy(isRainy)
                .sensor(sensor)
                .timeOfMeasurement(LocalDateTime.now())
                .build();
        measurementRepository.saveAndFlush(measurement);
    }

    public Integer countRainyDays() {
        return measurementRepository.findByIsRainyTrue().stream()
                .map(Measurement::getTimeOfMeasurement)
                .map(LocalDateTime::toLocalDate)
                .distinct()
                .toList().size();
    }
}
