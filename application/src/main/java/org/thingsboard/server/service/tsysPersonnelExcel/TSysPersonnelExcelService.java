package org.thingsboard.server.service.tsysPersonnelExcel;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface TSysPersonnelExcelService {

    void upload(MultipartFile file, String name);

    void downTemplate(HttpServletResponse response);
}
