package de.renber.yamlbundleeditor.importers.excel;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Layout;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.DumperOptions.ScalarStyle;

import de.renber.databinding.context.IDataContext;
import de.renber.databinding.context.beans.BeansDataContext;
import de.renber.databinding.templating.ITemplatingControlFactory;
import de.renber.quiterables.QuIterables;
import de.renber.yamlbundleeditor.exporters.IExportConfiguration;
import de.renber.yamlbundleeditor.exporters.excel.ExcelExportConfiguration;
import de.renber.yamlbundleeditor.importers.IImportConfiguration;
import de.renber.yamlbundleeditor.importers.IImporter;
import de.renber.yamlbundleeditor.importers.ImportException;
import de.renber.yamlbundleeditor.models.BundleCollection;
import de.renber.yamlbundleeditor.models.BundleMetaInfo;
import de.renber.yamlbundleeditor.models.LocalizedValue;
import de.renber.yamlbundleeditor.models.ResourceKey;
import de.renber.yamlbundleeditor.services.IDialogService;
import de.renber.yamlbundleeditor.services.ILocalizationService;
import de.renber.yamlbundleeditor.services.IconProvider;
import de.renber.yamlbundleeditor.services.impl.FileExtFilter;
import de.renber.yamlbundleeditor.utils.ResourceKeyUtils;

public class ExcelImporter implements IImporter {

	ILocalizationService loc;
	IDialogService dialogService;

	public ExcelImporter(ILocalizationService localizationService, IDialogService dialogService) {
		this.loc = localizationService;
		this.dialogService = dialogService;
	}

	@Override
	public String getName() {
		return "Microsoft Excel (*.xlsx)";
	}

	@Override
	public Image getImage() {
		return IconProvider.getImage("import/excel");
	}

	@Override
	public IImportConfiguration doImport(BundleCollection collection, IImportConfiguration configuration) throws ImportException {
		if (collection == null)
			throw new IllegalArgumentException("The parameter collection must not be null.");

		if (configuration == null || !(configuration instanceof ExcelImportConfiguration))
			throw new IllegalArgumentException("The parameter configuration must not be null and of type ExcelImportConfiguration.");

		ExcelImportConfiguration config = (ExcelImportConfiguration) configuration;

		File f = dialogService.showSaveFileDialog("Export as excel", new FileExtFilter("Excel-File", "*.xlsx"));
		if (f != null) {
			List<ImportBundleInfo> containedLanguages = getLanguagesFromFile(f);

			// show dialog for user to configure the import
			final ExcelImportConfigurationViewModel vm = new ExcelImportConfigurationViewModel(config, collection, containedLanguages);
			dialogService.showDialogFor(new BeansDataContext(vm), new ITemplatingControlFactory() {
				@Override
				public Control create(Composite parent, IDataContext itemDataContext) {
					return new ExcelImportConfigurationComposite(parent, SWT.None, itemDataContext);
				}

				@Override
				public Object getLayoutData(Layout parentLayout, Control itemControl, IDataContext itemDataContext) {
					return null;
				}
			});

			if (vm.isConfirmed()) {
				// do the import
				importFromExcelFile(f, collection, containedLanguages, config);
				return config;
			}
		}
		
		return null;
	}

	void importFromExcelFile(File file, BundleCollection targetCollection, List<ImportBundleInfo> bundles, ExcelImportConfiguration configuration) throws ImportException {
		try (XSSFWorkbook workBook = new XSSFWorkbook(file)) {
			XSSFSheet sheet = workBook.getSheetAt(0);

			// get the number of resource keys in the file
			int rowCount = sheet.getLastRowNum() + 1;

			for (ImportBundleInfo bundle : QuIterables.query(bundles).where(x -> x.includeInImport)) {
				// does this bundle already exist in the collection?
				BundleMetaInfo cBundle = QuIterables.query(targetCollection.getBundles()).firstOrDefault(x -> x.languageCode.equals(bundle.languageCode));
				if (cBundle == null) {
					// we import a bundle which has to be created first
					cBundle = new BundleMetaInfo();
					cBundle.languageCode = bundle.languageCode;
					cBundle.name = bundle.name;
					cBundle.author = "<imported>";
					targetCollection.getBundles().add(cBundle);
				}

				for (int rowIdx = 1; rowIdx < rowCount; rowIdx++) {
					XSSFRow row = sheet.getRow(rowIdx);
					String keyPath = getCellText(row.getCell(0));
					String localizedText = getCellText(row.getCell(bundle.excelColumn));

					String[] pathParts = keyPath.split("\\" + configuration.separator);

					ResourceKey rKey = ResourceKeyUtils.createPath(targetCollection, null, QuIterables.query(pathParts));
					LocalizedValue lv = QuIterables.query(rKey.getLocalizedValues()).firstOrDefault(x -> x.languageCode.equals(bundle.languageCode));
					if (lv == null) {
						lv = new LocalizedValue(bundle.languageCode, localizedText);
						rKey.getLocalizedValues().add(lv);
					} else
						lv.value = localizedText;
				}
			}
		} catch (Exception e) {
			throw new ImportException("Import failed: " + e.getMessage(), e);
		}
	}

	/**
	 * Returns the value of a cell as string Values of non-text cells are
	 * converted
	 */
	private String getCellText(XSSFCell cell) {
		switch (cell.getCellType()) {
		case XSSFCell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case XSSFCell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
				return String.valueOf(cell.getDateCellValue());

			return String.valueOf(cell.getNumericCellValue());
		case XSSFCell.CELL_TYPE_BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case XSSFCell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}

	/**
	 * Read what languages are contained in the file
	 * 
	 * @param f
	 * @return
	 */
	List<ImportBundleInfo> getLanguagesFromFile(File f) throws ImportException {
		try (XSSFWorkbook workBook = new XSSFWorkbook(f)) {
			// check which languages are contained in the excel file
			XSSFSheet sheet = workBook.getSheetAt(0);
			XSSFRow headerRow = sheet.getRow(0);

			if (!"Key".equals(headerRow.getCell(0).getStringCellValue()))
				throw new ImportException("Unsupported file");

			List<ImportBundleInfo> containedLanguages = new ArrayList<>();

			for (int rowCell = 1; rowCell < headerRow.getLastCellNum(); rowCell++) {
				// cell content is [languageCode] - [languageName]
				String cellContent = headerRow.getCell(rowCell).getStringCellValue();
				ImportBundleInfo bInfo = parseHeaderCell(cellContent);
				bInfo.excelColumn = rowCell;
				containedLanguages.add(bInfo);
			}

			return containedLanguages;

		} catch (InvalidFormatException e) {
			throw new ImportException("The format of the selected file is not supported: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new ImportException("Error while opening file: " + e.getMessage(), e);
		}
	}

	ImportBundleInfo parseHeaderCell(String cellContent) {
		ImportBundleInfo meta = new ImportBundleInfo();

		int hyphenIdx = cellContent.indexOf('-');
		if (hyphenIdx == -1) {
			meta.languageCode = cellContent;
		} else {
			meta.languageCode = cellContent.substring(0, hyphenIdx).trim();
			meta.name = cellContent.substring(hyphenIdx + 1).trim();
		}

		return meta;
	}

	@Override
	public BundleCollection doImport(IImportConfiguration configuration) throws ImportException {
		BundleCollection bundle = new BundleCollection();
		doImport(bundle, configuration);
		return bundle;
	}

	@Override
	public IImportConfiguration getDefaultConfiguration() {
		return new ExcelImportConfiguration();
	}

	@Override
	public String serializeConfiguration(IImportConfiguration configuration) {
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
	public IImportConfiguration deserializeConfiguration(String serializedString) {
		try (StringReader reader = new StringReader(serializedString)) {
			Yaml yaml = new Yaml();
			Object conf = yaml.load(reader);

			return (IImportConfiguration) conf;
		}
	}
}
