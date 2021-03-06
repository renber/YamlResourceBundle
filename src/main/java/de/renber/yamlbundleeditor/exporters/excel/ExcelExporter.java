package de.renber.yamlbundleeditor.exporters.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.renber.quiterables.QuIterables;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ColorScaleFormatting;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;

import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.yamlbundleeditor.exporters.ExportException;
import de.renber.yamlbundleeditor.exporters.IExportConfiguration;
import de.renber.yamlbundleeditor.exporters.IExporter;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.models.LocalizedValue;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;

public class ExcelExporter implements IExporter {

	ILocalizationService loc;
	IDialogService dialogService;

	public ExcelExporter(ILocalizationService localizationService, IDialogService dialogService) {
		this.loc = localizationService;
		this.dialogService = dialogService;
	}

	public String getName() {		
		return "Microsoft Excel (*.xlsx)";
	}
	
	public Image getImage() {
		return IconProvider.getImage("export/excel");
	}

	private List<String> filterLines;

	@Override
	public void doExport(BundleCollection collection, IExportConfiguration configuration) throws ExportException {
		if (collection == null)
			throw new IllegalArgumentException("The parameter collection must not be null.");
		
		if (configuration == null || !(configuration instanceof ExcelExportConfiguration))
				throw new IllegalArgumentException("The parameter configuration must not be null and of type ExcelExportConfiguration.");
		
		ExcelExportConfiguration config = (ExcelExportConfiguration)configuration;

		filterLines = new ArrayList<>();
		if (config.exportFilter != null && !config.exportFilter.isEmpty()) {
			for (String s : config.exportFilter.split("\\r?\\n")) {
				filterLines.add(s.toLowerCase());
			}
		}

		List<BundleMetaInfo> exportBundles = QuIterables.query(collection.getBundles()).where(x -> config.getLanguagesToExport().contains(x.languageCode)).toList();
		
		File f = dialogService.showSaveFileDialog("Export as excel", new FileExtFilter("Excel-File", "*.xlsx"));
		if (f != null) {
			try (XSSFWorkbook workBook = new XSSFWorkbook()) {
				XSSFSheet sheet = workBook.createSheet();

				// write the header row
				XSSFCellStyle headerCellStyle = workBook.createCellStyle();
				// foreground and background both belong to the cell background
				// (see
				// http://stackoverflow.com/questions/2803841/setting-foreground-color-for-hssfcellstyle-is-always-coming-out-black)
				headerCellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
				headerCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
				XSSFFont headerFont = workBook.createFont();
				headerFont.setBold(true);
				headerCellStyle.setFont(headerFont);

				{
					Row row = sheet.createRow(0);
					Cell cell = row.createCell(0);
					cell.setCellValue("Key");
					cell.setCellStyle(headerCellStyle);
					int cellNo = 1;
					for (BundleMetaInfo metaInfo : exportBundles) {
						cell = row.createCell(cellNo);
						cell.setCellValue(metaInfo.languageCode + " - " + metaInfo.localizedName);
						cell.setCellStyle(headerCellStyle);

						cellNo++;
					}
				}

				// write the resource keys
				int currRow = 1;
				Map<String, ResourceKey> keys = getFlatValues(collection.getValues(), "", config.levelSeparator);

				XSSFCellStyle multilineCellStyle = workBook.createCellStyle();
				multilineCellStyle.setWrapText(true);

				XSSFCellStyle missingCellStyle = workBook.createCellStyle();
				missingCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
				missingCellStyle.setFillPattern(CellStyle.THICK_FORWARD_DIAG);
				
				for (Entry<String, ResourceKey> entry : keys.entrySet()) {

					if (matchesFilter(entry.getKey(), filterLines)) {

						if (config.onlyExportKeysWithMissingValues) {
							boolean allSet = true;
							for (BundleMetaInfo metaInfo : exportBundles) {
								Object val = entry.getValue().getLocalizedValue(metaInfo.languageCode);
								if (val == null) {
									allSet = false;
									break;
								}
							}
							if (allSet) continue;
						}

						Row row = sheet.createRow(currRow);
						Cell cell = row.createCell(0);
						cell.setCellValue(entry.getKey());
						int maxCellLines = 1;
						int cellNo = 1;
						for (BundleMetaInfo metaInfo : exportBundles) {
							cell = row.createCell(cellNo);

							Object val = entry.getValue().getLocalizedValue(metaInfo.languageCode);
							if (val != null) {
								String v = val.toString();
								cell.setCellValue(v);
								if (v.contains("\n")) {
									cell.setCellStyle(multilineCellStyle);
									maxCellLines = Math.max(1, countLines(v));
								}
							}
							else {
								if (config.highlightMissingValues)
									cell.setCellStyle(missingCellStyle);
							}

							cellNo++;
						}

						row.setHeightInPoints(maxCellLines * sheet.getDefaultRowHeightInPoints());

						currRow++;
					}
				}

				// autosize columns
				XSSFRow row = sheet.getRow(0);
				for (int colNum = 0; colNum < row.getLastCellNum(); colNum++)
					sheet.autoSizeColumn(colNum);

				// write the excel file
				try (FileOutputStream fStream = new FileOutputStream(f)) {
					workBook.write(fStream);
				}
			} catch (IOException e) {
				throw new ExportException("Export failed.", e);
			}
		}
	}

	private int countLines(String s) {
		int cnt = 0;
		int idx = 0;
		while (idx >= 0) {
			cnt++;
			idx = s.indexOf("\n", idx + 1);
		}
		return cnt;
	}

	private boolean matchesFilter(String text, List<String> filters) {
		if (filterLines.size() == 0)
			return true;

		for(String filter: filters) {
			if (text.toLowerCase().contains(filter.toLowerCase()))
				return true;
		}

		return false;
	}

	/**
	 * Return all ResourceKeys which have at least one localized value as a flat
	 * list with their path
	 */
	private Map<String, ResourceKey> getFlatValues(List<ResourceKey> keys, String path, String separator) {
		TreeMap<String, ResourceKey> map = new TreeMap<>();

		for (ResourceKey key : keys) {
			if (key.hasChildren()) {
				Map<String, ResourceKey> subMap = getFlatValues(key.getChildren(), path + key.name + separator, separator);
				map.putAll(subMap);
			}

			if (!key.getLocalizedValues().isEmpty()) {
				map.put(path + key.name, key);
			}
		}

		return map;
	}

	@Override
	public IExportConfiguration getDefaultConfiguration() {
		return new ExcelExportConfiguration();
	}

	@Override
	public String serializeConfiguration(IExportConfiguration configuration) {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		options.setDefaultScalarStyle(ScalarStyle.PLAIN);

		Yaml yaml = new Yaml(options);
		try (Writer writer = new StringWriter()) {
			yaml.dump(configuration, writer);
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public IExportConfiguration deserializeConfiguration(String serializedString) {
		try (StringReader reader = new StringReader(serializedString)) {
			Yaml yaml = new Yaml();
			Object conf = yaml.load(reader);

			return (IExportConfiguration) conf;
		}
	}

	@Override
	public Control getConfigurationControl(Composite parent, BundleCollection collection, IExportConfiguration configuration) {
		if (parent == null)
			throw new IllegalArgumentException("Parameter parent must not be null.");
		
		if (collection == null)
			throw new IllegalArgumentException("Parameter collection must not be null.");
					
		if (configuration == null)
			throw new IllegalArgumentException("Parameter configuration must not be null.");
		
		ExcelExportConfigurationViewModel vm = new ExcelExportConfigurationViewModel((ExcelExportConfiguration)configuration, collection); 
		return new ExcelExportConfigurationComposite(parent, SWT.None, new BeansDataContext(vm));
	}

}
