package com.techacademy.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // 一覧取得メソッド
    public List<Report> findAll() {
        return reportRepository.findAll();
    }

    // ⭐ 新規保存メソッドを追加！
    public void save(Report report) {
        reportRepository.save(report);
    }
}
