package edu.tamu.app.controller;

import static edu.tamu.weaver.response.ApiStatus.ERROR;
import static edu.tamu.weaver.response.ApiStatus.SUCCESS;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.tamu.app.utilities.FileSystemUtility;
import edu.tamu.weaver.response.ApiResponse;

/**
 * Document Controller
 * 
 * @author
 *
 */
@RestController
@RequestMapping("/cv")
public class ControlledVocabularyController {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = Logger.getLogger(ControlledVocabularyController.class);

    /**
     * Get all controller vocabulary.
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/all")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getAllControlledVocabulary() {
        URL location = this.getClass().getResource("/config");
        String fullPath = FileSystemUtility.getWindowsSafePathString(location.getPath());

        String json = null;

        try {
            json = new String(readAllBytes(get(fullPath + File.separator + "cv.json")));
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        Map<String, Object> cvMap = null;

        try {
            cvMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            logger.error("Error reading cv json", e);
            return new ApiResponse(ERROR, "Error reading cv json");
        }

        return new ApiResponse(SUCCESS, cvMap);
    }

    /**
     * Get controlled vocabulary by label.
     * 
     * @param label
     * @ApiVariable String
     * 
     * @return ApiResponse
     * 
     */
    @RequestMapping("/{label}")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse getControlledVocabularyByField(@PathVariable String label) {
        URL location = this.getClass().getResource("/config");
        String fullPath = FileSystemUtility.getWindowsSafePathString(location.getPath());

        String json = null;

        try {
            json = new String(readAllBytes(get(fullPath + File.separator + "cv.json")));
        } catch (IOException e2) {
            logger.error("Error reading cv json", e2);
            return new ApiResponse(ERROR, "Error reading cv json");
        }

        Map<String, Object> cvMap = null;

        try {
            cvMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            logger.error("Error reading cv json value", e);
            return new ApiResponse(ERROR, "Error reading cv json value");
        }

        return new ApiResponse(SUCCESS, cvMap.get(label));
    }

}
