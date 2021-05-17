package com.example.demo.service;

import com.example.demo.repository.FakeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class CheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckService.class);
    private FakeRepository fakeRepository;

    @Autowired
    public CheckService(FakeRepository fakeRepository) {
        this.fakeRepository = fakeRepository;
    }

    public void check(HttpServletResponse response, int roomId, boolean entrance, int keyId) {
        /*
        Пропускная система.

        Необходимо реализовать сервер пропускной системы. Сервис общается с внешними интерфейсами посредством REST интерфейса.
        Сканер получает ид ключа пользователя и отправляет его на сервер, добавив номер двери и указание на вход или выход получает ответ, можно ли открыть дверь

        Ограничение: пользователь, не вышедший из одного помещения, не может войти в другое. Сервер должен логировать все действия пользователей.

        Приложение должно реализовать следующий интерфейс:
        http://localhost:8080/check?roomId=1&entrance=true&keyId=1
        где ответ 200 - дверь можно открыть
        403 - запрет на вход
        500 ошибка

        первоначальные данные:
        1-5 - существующие двери
        1-10000 - пользователи

        Пользователю можно входить только в те помещения, на номер которого делится его ид.
        */

        //1. Ошибка 500
        // - пользователь в комнате пытается зайти в другую комнату
        boolean userIntoRoom = this.fakeRepository.isIntoRoom(keyId);
        if (userIntoRoom && entrance) {
            LOGGER.info(String.format("Пользователь с id = %d пытается зайти в комнату %d, хотя он уже находится в какой-то комнате", keyId, roomId));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        // - пользователь в комнате пытается выйти из другой комнаты
        if (userIntoRoom) {
            int userRoom = this.fakeRepository.getUserRoom(keyId);
            //Пользователь хочет выйти из комнаты в которой находится
            if (userRoom == roomId) {
                LOGGER.info(String.format("Пользователь с id = %d вышел из комнаты %d", keyId, roomId));
                response.setStatus(HttpServletResponse.SC_OK);
                this.fakeRepository.removeUserFromRoom(roomId, keyId);
                return;
            } else {
                LOGGER.info(String.format("Пользователь с id = %d пытается выйти из комнаты %d, хотя он находится в комнате %d", keyId, roomId, userRoom));
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }
        }
        // - пользователь не в комнате пытается выйти из комнаты
        if (!entrance) {
            LOGGER.info(String.format("Пользователь с id = %d находится не в комнате и пытается выйти откуда-то", keyId));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        //2. Запрет на вход
        // - пользователь пытается зайти в комнату, в которую ему нет доступа
        if (keyId % roomId != 0) {
            LOGGER.info(String.format("Пользователь с id = %d пытается зайти в комнату %d. В доступе отказано.", keyId, roomId));
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            //3. Дверь можно открыть
            // пользователь не в комнате пытается зайти в комнату куда у него есть доступ
            // Запись в базу данных и в логи (в комнату roomId зашел пользователь keyId)
            LOGGER.info(String.format("Пользователь с id = %d зашел в комнату %d.", keyId, roomId));
            this.fakeRepository.saveToRoom(roomId, keyId);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
