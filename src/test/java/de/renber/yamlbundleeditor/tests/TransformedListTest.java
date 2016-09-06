package de.renber.yamlbundleeditor.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.junit.Before;
import org.junit.Test;

import de.renber.databinding.collections.TransformedList;
import de.renber.databinding.viewmodels.ViewModelBase;
import de.renber.yamlbundleeditor.tests.utils.JFaceTestCase;

public class TransformedListTest extends JFaceTestCase {	
	
	@Test
	public void test_transformedListCreate() {		
		List<TestModel> modelList = Arrays.asList(new TestModel[] {
				new TestModel(1),
				new TestModel(2),
				new TestModel(3)
		});
		
		IObservableList<TestViewModel> tList = new TransformedList<TestModel, TestViewModel>(modelList,
				(model) -> new TestViewModel(model),
				(viewmodel) -> viewmodel.getModel());
		
		assertEquals(3, tList.size());
		
		assertEquals(1, tList.get(0).getValue());
		assertEquals(2, tList.get(1).getValue());
		assertEquals(3, tList.get(2).getValue());
	}
	
	@Test
	public void test_transformedListAdd() {		
		List<TestModel> modelList = new ArrayList<TestModel>();
		
		IObservableList<TestViewModel> tList = new TransformedList<TestModel, TestViewModel>(modelList,
				(model) -> new TestViewModel(model),
				(viewmodel) -> viewmodel.getModel());
		
		assertTrue(tList.isEmpty());
		
		tList.add(new TestViewModel(new TestModel(23)));
		
		assertEquals(1, tList.size());
		assertEquals(23, tList.get(0).getValue());
		
		assertEquals(1, modelList.size());
		assertEquals(23, modelList.get(0).value);
	}
	
	@Test 
	public void test_transformedListInsert() {
		List<TestModel> modelList = new ArrayList<TestModel>(Arrays.asList(new TestModel[] {
				new TestModel(1),
				new TestModel(2),
				new TestModel(3)
		}));
		
		IObservableList<TestViewModel> tList = new TransformedList<TestModel, TestViewModel>(modelList,
				(model) -> new TestViewModel(model),
				(viewmodel) -> viewmodel.getModel());
		
		assertEquals(3, tList.size());
		
		tList.add(1, new TestViewModel(new TestModel(23)));
		
		assertEquals(4, tList.size());
		
		assertEquals(1, tList.get(0).getValue());
		assertEquals(23, tList.get(1).getValue());
		assertEquals(2, tList.get(2).getValue());
		assertEquals(3, tList.get(3).getValue());
		
		assertEquals(1, modelList.get(0).value);
		assertEquals(23, modelList.get(1).value);
		assertEquals(2, modelList.get(2).value);
		assertEquals(3, modelList.get(3).value);
	}
	
	@Test 
	public void test_transformedListRemove() {
		List<TestModel> modelList = new ArrayList<TestModel>(Arrays.asList(new TestModel[] {
				new TestModel(1),
				new TestModel(2),
				new TestModel(3)
		}));
		
		IObservableList<TestViewModel> tList = new TransformedList<TestModel, TestViewModel>(modelList,
				(model) -> new TestViewModel(model),
				(viewmodel) -> viewmodel.getModel());
		
		assertEquals(3, tList.size());
		
		tList.remove(1);
		
		assertEquals(2, tList.size());
		assertEquals(2, modelList.size());
		
		assertEquals(1, tList.get(0).getValue());
		assertEquals(3, tList.get(1).getValue());
		
		assertEquals(1, modelList.get(0).value);
		assertEquals(3, modelList.get(1).value);
	}
}

class TestModel {
	public int value;
	
	public TestModel(int value) {
		this.value = value;
	}
}

class TestViewModel extends ViewModelBase {
	TestModel model;
	
	public TestModel getModel() {
		return model;
	}
	
	public TestViewModel(TestModel model) {
		super();
		
		this.model = model;
	}
	
	public int getValue() {
		return model.value;
	}
	
	public void setValue(int newValue) {
		changeProperty("value", newValue);
	}
}