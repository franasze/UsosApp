package com.franaszekarkadiusz.UsosApp.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Service
public class UsosService {

    private static final String URL = "https://apps.usos.uj.edu.pl/services/courses/";
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LoggerFactory.getLogger(UsosService.class);
    private static final Map<String, String> subjectsWithLecturersMap = new HashMap<>();
    public String getData() {
        String course_id = "WF.IFO-IND-MON-01B";// for example
        String response = restTemplate.getForObject(URL + "course?course_id=" + course_id, String.class);
        log.info(response);
        isolateSubjectsAndLecturers(response);
        return response;
    }


    private void isolateSubjectsAndLecturers(String response) {
        String responseMock = """
                {
                    "subjects": [
                        {"subject": "Matematyka Dyskretna", "lecturer": "Adrian Kowalski"},
                        {"subject": "Algebra", "lecturer": "Beata Kowalska"},
                        {"subject": "Programowanie Obiektowe", "lecturer": "Cyprian Poznański"}
                        {"subject": "Fizyka", "lecturer": "Damian Krakowski"}
                        {"subject": "Teoria Grafów", "lecturer": "Emilia Warszawska"}
                        {"subject": "Sztuczna Inteligencja", "lecturer": "Feliks Gdański"}
                        {"subject": "Sieci Komputerowe", "lecturer": "Grażyna Lubelska"}
                        {"subject": "Bazy Danych", "lecturer": "Halina Szczecińska"}
                        {"subject": "Analiza Matematyczna", "lecturer": "Iwona Wrocławska"}
                        {"subject": "Systemy Operacyjne", "lecturer": "Jan Rzeszowski"}
                        {"subject": "Język Niemiecki", "lecturer": "Kamil Bydgoski"}
                    ]
                }
                """;

        Pattern pattern = Pattern.compile("\"subject\":\\s*\"(.*?)\".*?\"lecturer\":\\s*\"(.*?)\"");
        Matcher matcher = pattern.matcher(responseMock);
//        Matcher matcher = pattern.matcher(response);

        List<String> subjectNames = new ArrayList<>();
        List<String> lecturerNames = new ArrayList<>();
        while (matcher.find()) {
            subjectNames.add(matcher.group(1));
            lecturerNames.add(matcher.group(2));
        }

        if (!response.isEmpty()) {
            createMapFromLists(subjectNames, lecturerNames);

        }
    }

    private static void createMapFromLists(List<String> subjectNames, List<String> lecturerNames) {

        if (subjectNames.size() != lecturerNames.size()) {
            throw new IllegalArgumentException("Listy muszą mieć tą samą długość!");
        }

        for (int i = 0; i < subjectNames.size(); i++) {
            subjectsWithLecturersMap.put(subjectNames.get(i), lecturerNames.get(i));
        }


        Map<String, Integer> subjectToCommonCount = new HashMap<>();

        for (Map.Entry<String, String> entry : subjectsWithLecturersMap.entrySet()) {
            String subject = entry.getKey();
            String lecturer = entry.getValue();
            int commonCount = countCommonLetters(subject, lecturer);
            subjectToCommonCount.put(subject, commonCount);
        }

        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(subjectToCommonCount.entrySet());
        sortedEntries.sort((a, b) -> b.getValue() - a.getValue());

        printSolution(sortedEntries);
    }

    private static void printSolution(List<Map.Entry<String, Integer>> sortedEntries) {
        System.out.println("10 przedmiotów:");
        for (int i = 0; i < Math.min(10, sortedEntries.size()); i++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            System.out.println(entry.getKey() + " - " + entry.getValue() + " wspólnych liter (Prowadzący: " + subjectsWithLecturersMap.get(entry.getKey()) + ")");
        }
    }

    private static Map<Character, Integer> countLetters(String str) {
        Map<Character, Integer> letterCounts = new HashMap<>();
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                letterCounts.put(c, letterCounts.getOrDefault(c, 0) + 1);
            }
        }
        return letterCounts;
    }

    private static int countCommonLetters(String subject, String lecturer) {
        subject = subject.toLowerCase();
        lecturer = lecturer.toLowerCase();

        Map<Character, Integer> subjectLetterCounts = countLetters(subject);
        Map<Character, Integer> lecturerLetterCounts = countLetters(lecturer);

        int commonCount = 0;
        for (char c : subjectLetterCounts.keySet()) {
            if (lecturerLetterCounts.containsKey(c)) {
                commonCount += Math.min(subjectLetterCounts.get(c), lecturerLetterCounts.get(c));
            }
        }
        return commonCount;
    }
}
