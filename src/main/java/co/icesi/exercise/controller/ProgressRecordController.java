package co.icesi.exercise.controller;

import co.icesi.exercise.model.AppUser;
import co.icesi.exercise.model.nosql.ProgressRecordDocument;
import co.icesi.exercise.services.ProgressRecordService;
import co.icesi.exercise.services.RoutineService;
import co.icesi.exercise.services.UserService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/progress")
public class ProgressRecordController {

    @Autowired
    private ProgressRecordService progressRecordService;
    @Autowired
    private RoutineService routineService;
    @Autowired
    private UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("records", progressRecordService.getProgressRecordsByUserId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/new")
    public String newForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("record", new ProgressRecordDocument());
        model.addAttribute("routines", routineService.getRoutinesByOwnerId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@ModelAttribute ProgressRecordDocument record,
                         @RequestParam String routineId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        progressRecordService.createProgressRecord(record, userId, routineId);
        return "redirect:/progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model,
                           @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        model.addAttribute("record", progressRecordService.getProgressRecordById(id));
        model.addAttribute("routines", routineService.getRoutinesByOwnerId(userId));
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{id}")
    public String update(@PathVariable String id, @ModelAttribute ProgressRecordDocument record) {
        progressRecordService.updateProgressRecord(id, record);
        return "redirect:/progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        progressRecordService.deleteProgressRecord(id);
        return "redirect:/progress/list";
    }

    /** REQ 5 — trainer views a student's progress */
    @PreAuthorize("hasAuthority('VIEW_USERS')")
    @GetMapping("/student/{userId}")
    public String studentProgress(@PathVariable int userId, Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        AppUser student = userService.getAppUserById(userId);
        model.addAttribute("records", progressRecordService.getProgressRecordsByUserId(userId));
        model.addAttribute("student", student);
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/student-list";
    }

    /** REQ 11 — performance statistics with charts */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats")
    public String stats(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        List<ProgressRecordDocument> records = progressRecordService.getProgressRecordsByUserId(userId);
        records.sort(Comparator.comparing(r -> (r.getDate() != null ? r.getDate().getTime() : 0L)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        StringBuilder labels = new StringBuilder("[");
        StringBuilder repsData = new StringBuilder("[");
        StringBuilder weightData = new StringBuilder("[");

        for (int i = 0; i < records.size(); i++) {
            ProgressRecordDocument r = records.get(i);
            if (i > 0) { labels.append(","); repsData.append(","); weightData.append(","); }
            String dateStr = r.getDate() != null ? sdf.format(r.getDate()) : "?";
            labels.append("\"").append(dateStr).append("\"");
            repsData.append(r.getRepetitions() != null ? r.getRepetitions() : 0);
            weightData.append(r.getWeight() != null ? r.getWeight() : 0);
        }
        labels.append("]");
        repsData.append("]");
        weightData.append("]");

        model.addAttribute("records", records);
        model.addAttribute("labels", labels.toString());
        model.addAttribute("repsData", repsData.toString());
        model.addAttribute("weightData", weightData.toString());
        model.addAttribute("totalRecords", records.size());
        model.addAttribute("userName", userDetails.getUsername());
        return "progress/stats";
    }

    /** REQ 14 — export personal progress report as PDF */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response,
                          @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        AppUser user = userService.getAppUserById(userId);
        List<ProgressRecordDocument> records = progressRecordService.getProgressRecordsByUserId(userId);
        records.sort(Comparator.comparing(r -> (r.getDate() != null ? r.getDate().getTime() : 0L)));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte-progreso.pdf\"");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph(
                "Reporte de Progreso — " + user.getFirstName() + " " + user.getLastName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2.5f, 2.5f, 1f, 1f, 1.5f, 3f});

        Font hFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Color headerBg = new Color(175, 147, 214);
        for (String h : new String[]{"Fecha", "Ejercicio", "Rutina", "Series", "Reps", "Peso (kg)", "Notas"}) {
            PdfPCell cell = new PdfPCell(new Phrase(h, hFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(5);
            table.addCell(cell);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Font dFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (ProgressRecordDocument r : records) {
            table.addCell(new Phrase(r.getDate() != null ? sdf.format(r.getDate()) : "-", dFont));
            table.addCell(new Phrase(r.getExerciseName() != null ? r.getExerciseName() : "-", dFont));
            table.addCell(new Phrase(r.getRoutineName() != null ? r.getRoutineName() : "-", dFont));
            table.addCell(new Phrase(r.getSeries() != null ? r.getSeries().toString() : "-", dFont));
            table.addCell(new Phrase(r.getRepetitions() != null ? r.getRepetitions().toString() : "-", dFont));
            table.addCell(new Phrase(r.getWeight() != null ? r.getWeight().toString() : "-", dFont));
            table.addCell(new Phrase(r.getProgressNotes() != null ? r.getProgressNotes() : "-", dFont));
        }

        document.add(table);
        document.close();
    }
}
