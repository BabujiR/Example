package com.equal.exportapi.controller;

import com.equal.exportapi.entity.Person;
import com.equal.exportapi.service.PersonService;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    // CRUD endpoints
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> getAll() {
        return service.getAll();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getById(@PathVariable Long id) {
        Optional<Person> p = service.getById(id);
        return p.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Person create(@RequestBody Person person) {
        return service.save(person);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // CSV
    @GetMapping(value = "/export/csv", produces = "text/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=persons.csv");
        List<Person> persons = service.getAll();

        CSVPrinter printer = new CSVPrinter(response.getWriter(),
                CSVFormat.DEFAULT.withHeader("ID", "Name", "Age"));

        for (Person p : persons) {
            printer.printRecord(p.getId(), p.getName(), p.getAge());
        }
        printer.flush();
    }

    // Excel
    @GetMapping(value = "/export/excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=persons.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Persons");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("ID");
        header.createCell(1).setCellValue("Name");
        header.createCell(2).setCellValue("Age");

        int rowIdx = 1;
        for (Person p : service.getAll()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(p.getName());
            row.createCell(2).setCellValue(p.getAge());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // Text
    @GetMapping(value = "/export/txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportText() {
        StringBuilder sb = new StringBuilder();
        for (Person p : service.getAll()) {
            sb.append(p.getId()).append(" - ")
              .append(p.getName())
              .append(" (Age: ").append(p.getAge()).append(")")
              .append("\n");
        }
        return sb.toString();
    }

    // PDF
    @GetMapping(value = "/export/pdf", produces = "application/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=persons.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        for (Person p : service.getAll()) {
            document.add(new Paragraph(
                    "ID: " + p.getId() +
                    ", Name: " + p.getName() +
                    ", Age: " + p.getAge()
            ));
        }

        document.close();
    }
}
