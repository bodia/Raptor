package raptor.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import raptor.Quadrant;
import raptor.Raptor;
import raptor.RaptorWindowItem;
import raptor.pref.PreferenceKeys;

public class BrowserWindowItem implements RaptorWindowItem {

	protected Composite composite;
	protected Composite addressBar;
	protected Browser browser;
	protected String url;
	protected String title;

	public BrowserWindowItem(String title, String url) {
		this.url = url;
		this.title = title;
	}

	public void addItemChangedListener(ItemChangedListener listener) {
	}

	public boolean confirmClose() {
		return true;
	}

	public boolean confirmQuadrantMove() {
		return true;
	}

	public void dispose() {
		browser.dispose();
	}

	public Composite getControl() {
		return composite;
	}

	public Image getImage() {
		return null;
	}

	public Quadrant getPreferredQuadrant() {
		return Raptor.getInstance().getPreferences().getQuadrant(
				PreferenceKeys.APP_BROWSER_QUADRANT);
	}

	public String getTitle() {
		return title;
	}

	public void init(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(SWTUtils.createMarginlessGridLayout(1, false));

		addressBar = new Composite(composite, SWT.BORDER_SOLID);
		addressBar
				.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addressBar.setLayout(SWTUtils.createMarginlessGridLayout(4, false));

		Button backButton = new Button(addressBar, SWT.FLAT);
		backButton.setImage(Raptor.getInstance().getIcon("back"));
		backButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false,
				false));
		backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.back();
			}

		});

		Button forwardButton = new Button(addressBar, SWT.FLAT);
		forwardButton.setImage(Raptor.getInstance().getIcon("next"));
		forwardButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));

		forwardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.forward();
			}
		});

		Button refreshButton = new Button(addressBar, SWT.FLAT);
		refreshButton.setImage(Raptor.getInstance().getIcon("clockwise"));
		refreshButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER,
				false, false));

		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.refresh();
			}
		});

		final Text urlLabel = new Text(addressBar, SWT.SINGLE | SWT.BORDER);
		urlLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		urlLabel.setText(url);
		urlLabel.setEditable(false);

		browser = new Browser(composite, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browser.setUrl(url);
		browser.addLocationListener(new LocationAdapter() {
			public void changed(LocationEvent event) {
				urlLabel.setText(browser.getUrl());
			}
		});
	}

	public void onActivate() {
		System.err.println("On activate " + title);
		composite.layout(true);
		// browser.setUrl(url);
	}

	public void onPassivate() {
	}

	public void onReparent(Composite newParent) {
		url = browser.getUrl();
		composite.dispose();
		init(newParent);
	}

	public void removeItemChangedListener(ItemChangedListener listener) {
	}
}