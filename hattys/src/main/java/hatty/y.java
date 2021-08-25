package hatty;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ResourceMetaData;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

/**
 * Describe the reader here - this javadoc will appear as the description within
 * the reader's automatically generated UIMA XML descriptor file.
 * 
 * @author NaCTeM - National Centre of Text Mining
 */
@TypeCapability(inputs = {}, outputs = { "org.apache.uima.examples.SourceDocumentInformation" }) // Input and output annotation types
@ResourceMetaData(name = "Argo Example Reader")
public class y extends JCasCollectionReader_ImplBase {

	/**
	 * An example configuration parameter. For more information on how to use Apache
	 * uimaFIT annotations to define configuration parameters please see
	 * https://goo.gl/XsrA77 [uima.apache.org]. This javadoc will appear as the
	 * description of this parameter within the UIMA XML descriptor.
	 */
	public static final String PARAM_FILE_DIRECTORY = "directory";
	@ConfigurationParameter(name = PARAM_FILE_DIRECTORY, mandatory = true)
	private File directory;

	public static final String PARAM_RECURSIVE = "recursive";
	@ConfigurationParameter(name = PARAM_RECURSIVE, mandatory = false, defaultValue = "false")
	private boolean recursive;

	private Iterator<File> fileIterator;
	private int numProcessed;
	private int numTotal;

	/**
	 * Place any initialisation code in this method. This will be performed before
	 * any documents are processed. This method is optional and its definition in
	 * this class can be removed if no initialisation is required.
	 */
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		if (!directory.exists()) {
			throw new ResourceInitializationException(
					new RuntimeException("'" + directory.getAbsolutePath() + "' does not exist."));
		}

		if (!directory.isDirectory()) {
			throw new ResourceInitializationException(
					new RuntimeException("'" + directory.getAbsolutePath() + "' is not a directory."));
		}

		fileIterator = FileUtils.iterateFiles(directory, null, recursive);
		while (fileIterator.hasNext()) {
			fileIterator.next();
			numTotal++;
		}
		fileIterator = FileUtils.iterateFiles(directory, null, recursive);
	}

	public boolean hasNext() throws IOException, CollectionException {
		return fileIterator.hasNext();
	}

	public Progress[] getProgress() {
		return new Progress[] { new ProgressImpl(numProcessed, numTotal, Progress.ENTITIES) };
	}

	@Override
	public void getNext(JCas jCas) throws IOException, CollectionException {
		File file = fileIterator.next();
		String documentText = FileUtils.readFileToString(file);
		jCas.setDocumentText(documentText);
		
		SourceDocumentInformation docInfo = new SourceDocumentInformation(jCas);
		docInfo.setUri(file.toURI().toString());
		docInfo.addToIndexes();
	}
}