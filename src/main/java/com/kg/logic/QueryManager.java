package com.kg.logic;

import com.kg.exception.BadRequestException;
import com.kg.service.QueryService;
import com.kg.dto.QueryExportDto;
import com.kg.dto.QueryManagerExportDto;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class QueryManager implements QueryService {
    private static final String CSV_FILE = "csv";
    private static final String EXCEL_FILE = "xlsx";
    private static final String CSV_SEPARATOR = "¶";
    private static final String BAD_REQUEST = "The request could not be processed, verify that the data provided is correct. If not, make sure the mongo shell is responding correctly.";
    private static final Logger logger = LoggerFactory.getLogger(QueryManager.class);

    private List<List<String>> jsonParser(String jsonString) {

        List<List<String>> result = new ArrayList<>();
        jsonString = jsonString.replaceAll("ObjectId\\(|\\)", "");
        JSONArray jsonArray;
        if (jsonString.trim().startsWith("[")) {
            BsonArray bsonArray = BsonArray.parse(jsonString);
            jsonArray = new JSONObject(new BsonDocument("result", bsonArray).toJson()).getJSONArray("result");
        } else {
            BsonDocument bsonDocument = BsonDocument.parse(jsonString);
            Document document = Document.parse(bsonDocument.toJson());
            jsonArray = new JSONArray();
            jsonArray.put(new JSONObject(document.toJson()));
        }

        Set<String> keys = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            keys.addAll(jsonObject.keySet());
        }

        List<String> header = new ArrayList<>(keys);
        result.add(header);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            List<String> row = new ArrayList<>();
            for (String key : header) {
                if (jsonObject.has(key)) {
                    row.add(jsonObject.get(key).toString());
                } else {
                    row.add(null);
                }
            }
            result.add(row);
        }

        return result;
    }
    private String decode(String base64String) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        return new String(decodedBytes);
    }

    private boolean isListQuery(String text) {
        String regex = "\\b(findOne|findAndModify|findOneAndUpdate|findOneAndReplace|findOneAndDelete)\\b";
        return !Pattern.compile(regex).matcher(text).find();
    }

    @Override
    public QueryManagerExportDto export(QueryExportDto queryExportDto) {

        String uri = queryExportDto.getUri();
        String query = decode(queryExportDto.getQuery())
                .replaceAll("\"", "'")
                .replaceAll("\\s"," ")
                .replaceAll("\n","")
                .trim();

        if(isListQuery(query)) {
            query = query.concat(".toArray()");
        }

        String format = queryExportDto.getFormat();
        Runtime runtime = Runtime.getRuntime();
        String[] command = {"mongosh", uri, "--eval", "printjson(" + query + ")", "--quiet"};

        try {

            Process process = runtime.exec(command);
            byte[] data = process.getInputStream().readAllBytes();
            String result = new String(data, StandardCharsets.UTF_8);
            List<List<String>> dataParser = jsonParser(result);

            if (CSV_FILE.equalsIgnoreCase(format)) {
                logger.debug("csv file ...");
                return toCsv(dataParser);
            }

            if (EXCEL_FILE.equalsIgnoreCase(format)) {
                logger.debug("excel file ...");
                return toExcel(dataParser);
            }

        } catch (IOException e) {
            logger.error(e.toString());
        }

        throw new BadRequestException(BAD_REQUEST);
    }

    private QueryManagerExportDto toExcel(List<List<String>> dataParser) {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheets = workbook.createSheet("data");

            for (int i = 0; i < dataParser.size(); i++) {
                Row row = sheets.createRow(i);
                List<String> rowData = dataParser.get(i);
                for (int j = 0; j < rowData.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData.get(j));
                }
            }

            workbook.write(outputStream);
            return new QueryManagerExportDto(outputStream.toByteArray(), "data.xlsx");

        } catch (IOException e) {
            logger.error(e.toString());
        }

        throw new BadRequestException(BAD_REQUEST);
    }

    private QueryManagerExportDto toCsv(List<List<String>> dataParser) {
        StringBuilder stringBuilder = new StringBuilder();
        dataParser.forEach(line -> {
            stringBuilder.append(String.join(CSV_SEPARATOR, line));
            stringBuilder.append("\n");
        });

        byte[] content = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
        return new QueryManagerExportDto(content, "data.csv");
    }
}
