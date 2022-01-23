package de.patternizer.eclipse.patterns.visitor;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.patternizer.eclipse.patterns.PatternConfigData;
import de.patternizer.eclipse.patterns.PatternConfigPage;
import de.patternizer.eclipse.patterns.PatternConfigPagePlugin;
import de.patternizer.eclipse.patterns.helpers.ASTManipulationHelper;
import de.patternizer.eclipse.patterns.helpers.gui.CustomContentProposalAdapter;
import de.patternizer.eclipse.patterns.visitor.VisitorConfigData.Goal;

//TODO this needs cleanup and documentation

/**
 * This class handles the widgets responsible for configuration of any
 * Visitor-specific config data.
 * 
 * @see VisitorConfigData
 * 
 * @author Alexander Kalinowski
 *
 */
public class VisitorConfigPagePlugin extends PatternConfigPagePlugin
{
	// FIELDS
	Button radioButtonCurrentAsVisitor = null;
	Button radioButtonCurrentAsVisitee = null;
	
	Label lblVisitorBaseClassdentifier = null;
	Text visitorBaseClassIdentifierText = null;
	
	Label lblVisitorHostBaseClassdentifier = null;
	Text visitorHostBaseClassIdentifierText = null;
	
	Label lblVisitorTreeDescriptor = null;
	Tree tree = null;
	
	// CONSTRUCTORS
	public VisitorConfigPagePlugin()
	{}
	
	@Override
	protected void initComponents(Composite parentComposite)
	{
		// sanity check before type casting
		if (!(getPatternConfigData() instanceof VisitorConfigData))
			throw new IllegalStateException("configData in initComponents() is not of the type VisitorConfigData!");
		VisitorConfigData configData = (VisitorConfigData) getPatternConfigData();
		
		// user must select classes before he can finish
		// TODO properly handle activation/deactivation of this
		this.getParentConfigPage().setPageComplete(false);
		
		
		
		
		// radio buttons
		Group visitorVisiteeRBGroup = new Group(parentComposite, SWT.NONE);
		visitorVisiteeRBGroup.setLayout(new GridLayout(3, false));
		visitorVisiteeRBGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 7));
		
		Label lblVisitorVisiteeRB = new Label(visitorVisiteeRBGroup, SWT.NONE);
		lblVisitorVisiteeRB.setText("Configure the currently selected class to become");
		new Label(visitorVisiteeRBGroup, SWT.NONE);
		new Label(visitorVisiteeRBGroup, SWT.NONE);
		
		radioButtonCurrentAsVisitor = new Button(visitorVisiteeRBGroup, SWT.RADIO);
		radioButtonCurrentAsVisitor.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				configData.setCurrentTypeGoal(Goal.VISITOR);
				lblVisitorBaseClassdentifier.setText("Visitor base class:");
				lblVisitorHostBaseClassdentifier.setText("Visitor host base class:");
			}
		});
		radioButtonCurrentAsVisitor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 9, 1));
		radioButtonCurrentAsVisitor.setText("a Visitor class");
		radioButtonCurrentAsVisitor.setSelection(true);
		
		radioButtonCurrentAsVisitee = new Button(visitorVisiteeRBGroup, SWT.RADIO);
		radioButtonCurrentAsVisitee.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				configData.setCurrentTypeGoal(Goal.VISITEE);
				lblVisitorBaseClassdentifier.setText("Visitor host base class:");
				lblVisitorHostBaseClassdentifier.setText("Visitor base class:");
			}
		});
		radioButtonCurrentAsVisitee.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		radioButtonCurrentAsVisitee.setText("a Visitee (Visitor Host) class");
		
		new Label(parentComposite, SWT.NONE);
		new Label(parentComposite, SWT.NONE);
		
		
		
		// Label/text for Tree
		lblVisitorBaseClassdentifier = new Label(parentComposite, SWT.NONE);
		lblVisitorBaseClassdentifier.setText("Visitor base class:                  ");
		
		visitorBaseClassIdentifierText = new Text(parentComposite, SWT.BORDER | SWT.NO_FOCUS);
		visitorBaseClassIdentifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		visitorBaseClassIdentifierText.setText("");
		// visitorBaseClassIdentifierText.setSelection(0, 100);
		visitorBaseClassIdentifierText.setEditable(false);
		// visitorBaseClassIdentifierText.setEnabled(false);
		visitorBaseClassIdentifierText.setBackground(new Color(255, 255, 255));
		
		
		
		// Tree
		lblVisitorTreeDescriptor = new Label(parentComposite, SWT.NONE);
		lblVisitorTreeDescriptor.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		lblVisitorTreeDescriptor.setText("Select the currently selected class or one of its ancestors:");
		
		tree = new Tree(parentComposite, SWT.SINGLE);
		GridData treeLayout = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 6);
		treeLayout.heightHint = 150;
		tree.setLayoutData(treeLayout);
		
		
		ITypeHierarchy typeHierarchy = ASTManipulationHelper.getCurrentTypeHierarchy(((VisitorConfigData) configData).getInsertionHelper());
		initTreeWithTypeHierarchy(typeHierarchy);
		
		// yes, a bit long for a lambda but surely quicker to grasp than a full
		// implementation of SelectionListener
		tree.addSelectionListener(SelectionListener.widgetSelectedAdapter((event) ->
		{
			if (event.item instanceof TreeItem)
			{
				IType type = ((IType) ((TreeItem) event.item).getData());
				visitorBaseClassIdentifierText.setText(type.getFullyQualifiedName());
				((VisitorConfigData) configData).setVisitorBaseType(type);
				
			}
			else if (event.item instanceof Tree)
			{
				IType type = ((IType) ((Tree) event.item).getTopItem().getData());
				visitorBaseClassIdentifierText.setText(type.getFullyQualifiedName());
				((VisitorConfigData) configData).setVisitorBaseType(type);
			}
		}));
		tree.setFocus();
		tree.setSelection(tree.getTopItem());
		Event selEvent = new Event();
		selEvent.item = tree;
		selEvent.widget = tree;
		tree.notifyListeners(SWT.Selection, selEvent);
		
		new Label(parentComposite, SWT.NONE);
		new Label(parentComposite, SWT.NONE);
		
		
		
		
		
		// Visitee label and text
		lblVisitorHostBaseClassdentifier = new Label(parentComposite, SWT.NONE);
		lblVisitorHostBaseClassdentifier.setText("Visitor host base class:              ");
		
		visitorHostBaseClassIdentifierText = new Text(parentComposite, SWT.BORDER);
		visitorHostBaseClassIdentifierText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		visitorHostBaseClassIdentifierText.setText("");
		visitorHostBaseClassIdentifierText.setSelection(0, 100);
		
		// TODO do I even still need this?
		// CustomContentProposalAdapter contentProposalAdapter =
		// (CustomContentProposalAdapter)
		addAutocomplete(visitorHostBaseClassIdentifierText, configData, getParentConfigPage());
		
		new Label(parentComposite, SWT.NONE);
		new Label(parentComposite, SWT.NONE);
		
		
		
	}
	
	class SimpleListener implements Listener
	{
		CustomContentProposalAdapter contentProposalAdapter;
		
		public SimpleListener(CustomContentProposalAdapter contentProposalAdapter)
		{
			this.contentProposalAdapter = contentProposalAdapter;
		}
		
		public void handleEvent(Event e)
		{
			// System.out.println("Mouse down somewhere!");
			Event untypedEvent = new Event();
			untypedEvent.type = SWT.MouseDown;
			untypedEvent.display = e.display;
			untypedEvent.widget = e.widget;
			untypedEvent.time = e.time;
			if (contentProposalAdapter != null && contentProposalAdapter.popup != null && contentProposalAdapter.popup.popupCloser != null)
				contentProposalAdapter.popup.popupCloser.handleEvent(untypedEvent);
		}
		
	}
	
	private ContentProposalAdapter addAutocomplete(Text visitorHostBaseClassIdentifierText, VisitorConfigData configData, PatternConfigPage page)
	{
		//@formatter:off		
		List<String> typeNames = configData.getUnits()
			.stream()
			.map(ICompilationUnit::getElementName)
			.collect(Collectors.toList());
		//@formatter:on		
		SimpleContentProposalProvider propProvider = new SimpleContentProposalProvider((String[]) typeNames.toArray(new String[0]));
		propProvider.setFiltering(true);
		CustomContentProposalAdapter contentProposalAdapter = new CustomContentProposalAdapter(visitorHostBaseClassIdentifierText, new TextContentAdapter(),
				propProvider, null, null);
		contentProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		
		visitorHostBaseClassIdentifierText.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				// TODO Auto-generated method stub
				String currentText = visitorHostBaseClassIdentifierText.getText();
				if (currentText.equals(""))
				{
					if (tree.getSelectionCount() == 1) page.setPageComplete(true);
					configData.setVisiteeBaseType(null);
				}
				else
				{
					int index = typeNames.indexOf(currentText);
					if (index > -1)
					{
						if (tree.getSelectionCount() == 1) page.setPageComplete(true);
						IType type = configData.getUnits().get(index).findPrimaryType();
						configData.setVisiteeBaseType(type);
						// System.out.println("Visitee: " + currentText + " / Type: " + type);
					}
					else page.setPageComplete(false);
				}
				
			}
			
		});
		
		visitorHostBaseClassIdentifierText.addMouseListener(new MouseListener()
		{
			
			@Override
			public void mouseDoubleClick(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDown(MouseEvent e)
			{
				// TODO Auto-generated method stub
				if (!contentProposalAdapter.isProposalPopupOpen()) contentProposalAdapter.openProposalPopup();
				else contentProposalAdapter.closeProposalPopup();
			}
			
			@Override
			public void mouseUp(MouseEvent e)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		visitorHostBaseClassIdentifierText.addFocusListener(new FocusListener()
		{
			
			@Override
			public void focusGained(FocusEvent e)
			{
				// TODO Auto-generated method stub
				// System.out.println("Focus gained!");
				// contentProposalAdapter.openProposalPopup();
			}
			
			@Override
			public void focusLost(FocusEvent e)
			{
				// TODO Auto-generated method stub
				// System.out.println("Focus lost!");
				// contentProposalAdapter.closeProposalPopup();
			}
			
		});
		
		return contentProposalAdapter;
	}
	
	@Override
	protected void initDatabinding(DataBindingContext dataBindingContext, PatternConfigData configData)
	{
		// var visitorBaseClassObserveWidget =
		// WidgetProperties.text(SWT.Modify).observe(visitorBaseClassIdentifierText);
		// var visitorBaseClassObserveValue =
		// PojoProperties.value("visitorBaseClass").observe(configData);
		// dataBindingContext.bindValue(visitorBaseClassObserveWidget,
		// visitorBaseClassObserveValue, null, null);
		
		// var visitorHostBaseClassObserveWidget =
		// WidgetProperties.text(SWT.Modify).observe(visitorHostBaseClassIdentifierText);
		// var visitorHostBaseClassObserveValue =
		// PojoProperties.value("visitorHostBaseClass").observe(configData);
		// dataBindingContext.bindValue(visitorHostBaseClassObserveWidget,
		// visitorHostBaseClassObserveValue, null, null);
		
	}
	
	@Override
	protected void cleanUpConfigPage(String previouslySelectedTypeClassname)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void setupConfigPage(String newlySelectedTypeClassname)
	{
		// TODO Auto-generated method stub
	}
	
	
	
	// HELPER METHODS
	// not sophisticated but implemented faster than reading into
	// TreeViewer/ITreeContentProvider, etc
	public void initTreeWithTypeHierarchy(ITypeHierarchy typeHierarchy)
	{
		IType root = typeHierarchy.getType();
		assert (ASTManipulationHelper.isInWorkspace(root.getFullyQualifiedName()));
		
		TreeItem treeItem = new TreeItem(tree, SWT.NULL);
		treeItem.setText(root.getElementName());
		treeItem.setData(root);
		treeItem.setExpanded(true); // for some reason this fails to expand the item at this stage, maybe because
									// not everything has been properly initialized
		
		IType superclass = typeHierarchy.getSuperclass(root);
		traverseTypeTree(treeItem, superclass, typeHierarchy);
		
		IType[] superInterfaces = typeHierarchy.getSuperInterfaces(root);
		for (IType superInterface : superInterfaces)
		{
			traverseTypeTree(treeItem, superInterface, typeHierarchy);
		}
	}
	
	private void traverseTypeTree(TreeItem parentItem, IType type, ITypeHierarchy typeHierarchy)
	{
		if (type == null) return;
		if (!(ASTManipulationHelper.isInWorkspace(type.getFullyQualifiedName()))) return;
		
		TreeItem treeItem = new TreeItem(parentItem, SWT.NULL);
		treeItem.setText(type.getElementName());
		treeItem.setData(type);
		treeItem.setExpanded(true); // for some reason this fails to expand the item at this stage, maybe because
									// not everything has been properly initialized
		
		IType superclass = typeHierarchy.getSuperclass(type);
		traverseTypeTree(treeItem, superclass, typeHierarchy);
		
		IType[] superInterfaces = typeHierarchy.getSuperInterfaces(type);
		for (IType superInterface : superInterfaces)
		{
			traverseTypeTree(treeItem, superInterface, typeHierarchy);
		}
	}
	
	public Tree getTree()
	{
		return tree;
	}
	
	
	
}
