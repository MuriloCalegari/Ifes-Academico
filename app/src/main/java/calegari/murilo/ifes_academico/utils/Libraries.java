package calegari.murilo.ifes_academico.utils;

import com.franmontiel.attributionpresenter.entities.Attribution;
import com.franmontiel.attributionpresenter.entities.License;

public enum Libraries {
	VERTICAL_STEPPER_FORM((new Attribution.Builder("Vertical Stepper Form").addCopyrightNotice("Copyright 2018 Julio Ernesto Cabañas").addLicense(License.APACHE).setWebsite("https://github.com/ernestoyaquello/VerticalStepperForm")).build()),
	MATERIAL_SEEK_BAR_PREFERENCE((new Attribution.Builder("Material SeekBarPreference").addCopyrightNotice("Copyright 2016 Pavel Sikun").addLicense(License.APACHE).setWebsite("https://github.com/MrBIMC/MaterialSeekBarPreference")).build());

	private Attribution attribution;

	private Libraries(Attribution attribution) {
		this.attribution = attribution;
	}

	public Attribution getAttribution() {
		return this.attribution;
	}
}
