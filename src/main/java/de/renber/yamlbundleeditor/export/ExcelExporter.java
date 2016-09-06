package de.renber.yamlbundleeditor.export;

import java.awt.Composite;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;

public class ExcelExporter implements IExporter {

	ILocalizationService locService;

	public ExcelExporter(ILocalizationService localizationService) {
		locService = localizationService;
	}

	@Override
	public FileExtFilter[] getSupportedFileTypes() {
		return new FileExtFilter[] { new FileExtFilter(locService.getString("export:excel:filetype"), "*.xlsx") };
	}

	@Override
	public void export(OutputStream stream, BundleCollection collection, String exportExtension) throws ExportException {
		if (!"*.xlsx".equals(exportExtension))
			throw new ExportException("Unsupported file type: " + exportExtension);

		try (XSSFWorkbook workBook = new XSSFWorkbook()) {
			XSSFSheet sheet = workBook.createSheet();

			// header row
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("Key");
			int cellNo = 1;
			for (BundleMetaInfo metaInfo : collection.getBundles()) {
				cell = row.createCell(cellNo);
				cell.setCellValue(metaInfo.languageCode + " - " + metaInfo.localizedName);

				cellNo++;
			}

			// write the excel file			
			workBook.write(stream);		
		} catch (IOException e) {
			throw new ExportException("Export failed.", e);
		}
	}

	@Override
	public Composite getConfigurationComposite() {
		return null;
	}

}
