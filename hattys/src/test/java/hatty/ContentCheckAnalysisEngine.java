package hatty;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import static org.apache.uima.fit.util.JCasUtil.selectSingle;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;

public class ContentCheckAnalysisEngine extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas jcas) throws AnalysisEngineProcessException {
		SourceDocumentInformation docInfo = selectSingle(jcas, SourceDocumentInformation.class);
		try {
			File file = new File(new URI(docInfo.getUri()));
			String documentText = FileUtils.readFileToString(file);
			if (!documentText.equals(jcas.getDocumentText())) {
				throw new RuntimeException("Document text does not match expected value.");
			}
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
