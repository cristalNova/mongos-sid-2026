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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.Color;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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
    public String list(Model model,
                       @AuthenticationPrincipal UserDetails userDetails,
                       @RequestParam(required = false) String q) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        var records = progressRecordService.getProgressRecordsByUserId(userId);
        if (q != null && !q.isBlank()) {
            String lower = q.toLowerCase();
            records = records.stream()
                    .filter(r -> (r.getExerciseName() != null && r.getExerciseName().toLowerCase().contains(lower))
                              || (r.getRoutineName() != null && r.getRoutineName().toLowerCase().contains(lower)))
                    .toList();
        }
        model.addAttribute("records", records);
        model.addAttribute("q", q);
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
                         @AuthenticationPrincipal UserDetails userDetails,
                         RedirectAttributes ra) {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        progressRecordService.createProgressRecord(record, userId, routineId);
        ra.addFlashAttribute("flashSuccess", "Registro de progreso creado correctamente.");
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
    public String update(@PathVariable String id, @ModelAttribute ProgressRecordDocument record,
                         RedirectAttributes ra) {
        progressRecordService.updateProgressRecord(id, record);
        ra.addFlashAttribute("flashSuccess", "Registro de progreso actualizado.");
        return "redirect:/progress/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        progressRecordService.deleteProgressRecord(id);
        ra.addFlashAttribute("flashSuccess", "Registro de progreso eliminado.");
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

    private List<ProgressRecordDocument> filterRecords(
            List<ProgressRecordDocument> allRecords,
            String period,
            String routine,
            String exercise
    ) {

        Date now = new Date();

        long filterTime = switch (period) {
            case "week" -> now.getTime() - (7L * 24 * 60 * 60 * 1000);
            case "month" -> now.getTime() - (30L * 24 * 60 * 60 * 1000);
            default -> 0;
        };

        return allRecords.stream()
                .filter(r -> period.equals("all") ||
                        (r.getDate() != null && r.getDate().getTime() >= filterTime))
                .filter(r -> routine == null || routine.isEmpty()
                        || routine.equals(r.getRoutineName()))
                .filter(r -> exercise == null || exercise.isEmpty()
                        || exercise.equals(r.getExerciseName()))
                .sorted(Comparator.comparing(r ->
                        r.getDate() != null ? r.getDate().getTime() : 0L))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/stats")
    public String stats(
            @RequestParam(defaultValue = "all") String period,
            @RequestParam(required = false) String routine,
            @RequestParam(required = false) String exercise,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();

        List<ProgressRecordDocument> allRecords =
                progressRecordService.getProgressRecordsByUserId(userId);

        List<ProgressRecordDocument> records =
                filterRecords(allRecords, period, routine, exercise);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

        List<String> labels = new ArrayList<>();
        List<Integer> repsData = new ArrayList<>();
        List<Double> weightData = new ArrayList<>();
        List<Integer> seriesData = new ArrayList<>();

        for (ProgressRecordDocument r : records) {
            labels.add(r.getDate() != null ? sdf.format(r.getDate()) : "?");
            repsData.add(r.getRepetitions() != null ? r.getRepetitions() : 0);
            weightData.add(r.getWeight() != null ? r.getWeight() : 0);
            seriesData.add(r.getSeries() != null ? r.getSeries() : 0);
        }

        double avgReps = records.stream()
                .filter(r -> r.getRepetitions() != null)
                .mapToInt(ProgressRecordDocument::getRepetitions)
                .average().orElse(0);

        double avgWeight = records.stream()
                .filter(r -> r.getWeight() != null)
                .mapToDouble(ProgressRecordDocument::getWeight)
                .average().orElse(0);

        long totalReps = records.stream()
                .filter(r -> r.getRepetitions() != null)
                .mapToLong(ProgressRecordDocument::getRepetitions)
                .sum();

        long totalSeries = records.stream()
                .filter(r -> r.getSeries() != null)
                .mapToLong(ProgressRecordDocument::getSeries)
                .sum();

        Map<String, List<Integer>> sparksMap = records.stream()
                .filter(r -> r.getExerciseName() != null && r.getRepetitions() != null)
                .collect(Collectors.groupingBy(
                        ProgressRecordDocument::getExerciseName,
                        LinkedHashMap::new,
                        Collectors.mapping(ProgressRecordDocument::getRepetitions, Collectors.toList())
                ));

        List<Map<String, Object>> exerciseSparks = new ArrayList<>();

        for (var entry : sparksMap.entrySet()) {

            List<Integer> vals = entry.getValue();
            int max = vals.stream().mapToInt(Integer::intValue).max().orElse(1);

            StringBuilder pts = new StringBuilder();

            for (int i = 0; i < vals.size(); i++) {

                double x = vals.size() == 1 ? 40.0 : (i * 80.0 / (vals.size() - 1));
                double y = 28.0 - (vals.get(i) * 28.0 / max);

                if (i > 0) pts.append(" ");

                pts.append(String.format("%.1f,%.1f", x, y));
            }

            double avg = vals.stream().mapToInt(Integer::intValue).average().orElse(0);

            Map<String, Object> spark = new LinkedHashMap<>();

            spark.put("name", entry.getKey());
            spark.put("points", pts.toString());
            spark.put("count", vals.size());
            spark.put("avgReps", Math.round(avg));
            spark.put("maxReps", max);

            exerciseSparks.add(spark);
        }

        model.addAttribute("records", records);
        model.addAttribute("labels", labels);
        model.addAttribute("repsData", repsData);
        model.addAttribute("weightData", weightData);
        model.addAttribute("seriesData", seriesData);

        model.addAttribute("totalRecords", records.size());
        model.addAttribute("totalReps", totalReps);
        model.addAttribute("totalSeries", totalSeries);

        model.addAttribute("avgReps", Math.round(avgReps));
        model.addAttribute("avgWeight", String.format("%.1f", avgWeight));

        model.addAttribute("exerciseSparks", exerciseSparks);

        model.addAttribute("routines", allRecords.stream().map(ProgressRecordDocument::getRoutineName).filter(Objects::nonNull).distinct().toList());
        model.addAttribute("exercises", allRecords.stream().map(ProgressRecordDocument::getExerciseName).filter(Objects::nonNull).distinct().toList());

        model.addAttribute("selectedRoutine", routine);
        model.addAttribute("selectedExercise", exercise);

        model.addAttribute("userName", userDetails.getUsername());
        model.addAttribute("period", period);

        return "progress/stats";
    }
    /** REQ 14 — export personal progress report as PDF */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/export/pdf")
    public void exportPdf(@RequestParam(defaultValue = "all") String period,
                          @RequestParam(required = false) String routine,
                          @RequestParam(required = false) String exercise,HttpServletResponse response,
                          @AuthenticationPrincipal UserDetails userDetails) throws Exception {
        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        AppUser user = userService.getAppUserById(userId);
        List<ProgressRecordDocument> allRecords = progressRecordService.getProgressRecordsByUserId(userId);

        List<ProgressRecordDocument> records =
                filterRecords(allRecords, period, routine, exercise);

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/export/csv")
    public void exportCsv(@RequestParam(required = false) String q,HttpServletResponse response,
                          @AuthenticationPrincipal UserDetails userDetails) throws Exception {

        int userId = userService.getUserByEmail(userDetails.getUsername()).getId();
        List<ProgressRecordDocument> records = progressRecordService.getProgressRecordsByUserId(userId);

        if (q != null && !q.isBlank()) {
            String lower = q.toLowerCase();
            records = records.stream()
                    .filter(r -> (r.getExerciseName() != null && r.getExerciseName().toLowerCase().contains(lower))
                            || (r.getRoutineName() != null && r.getRoutineName().toLowerCase().contains(lower)))
                    .toList();
        }

        records = records.stream()
                .sorted(Comparator.comparing(r ->
                        r.getDate() != null ? r.getDate().getTime() : 0L))
                .toList();


        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"reporte-progreso.csv\"");

        PrintWriter writer = response.getWriter();

        writer.println("Fecha,Ejercicio,Rutina,Series,Reps,Peso (kg),Notas");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (ProgressRecordDocument r : records) {

            writer.println(String.join(",",
                    r.getDate() != null ? sdf.format(r.getDate()) : "",
                    r.getExerciseName() != null ? r.getExerciseName() : "",
                    r.getRoutineName() != null ? r.getRoutineName() : "",
                    String.valueOf(r.getSeries() != null ? r.getSeries() : 0),
                    String.valueOf(r.getRepetitions() != null ? r.getRepetitions() : 0),
                    String.valueOf(r.getWeight() != null ? r.getWeight() : 0),
                    r.getProgressNotes() != null ? r.getProgressNotes().replace(",", " ") : ""
            ));
        }

        writer.flush();
        writer.close();
    }
}
