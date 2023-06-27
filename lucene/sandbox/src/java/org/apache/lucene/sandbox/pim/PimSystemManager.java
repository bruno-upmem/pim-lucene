package org.apache.lucene.sandbox.pim;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafSimScorer;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.List;

public interface PimSystemManager {

    static PimSystemManager get() {
        return PimSystemManager1.get();
    }

    /**
     * Tells whether the current PIM index loaded is
     * the right one to answer queries for the LeafReaderContext object
     */
    boolean isReady(LeafReaderContext context);

    /**
     * Information on which query types are supported by the PIM system
     *
     * @param query the input query
     * @return true if the query is supported by the PIM system
     */
    boolean isQuerySupported(Query query);

    /**
     * Load the pim index unless one is already loaded
     *
     * @param pimDirectory the directory containing the PIM index
     * @return true if the index was successfully loaded
     */
    boolean loadPimIndex(Directory pimDirectory) throws IOException;

    /**
     * Unload the PIM index if currently loaded
     *
     * @return true if the index has been unloaded
     */
    boolean unloadPimIndex();

    void shutDown();

    <QueryType extends Query & PimQuery> List<PimMatch> search(LeafReaderContext context,
                                                               QueryType query,
                                                               LeafSimScorer scorer)
            throws PimQueryQueueFullException, InterruptedException;

    /**
     * Custom Exception to be thrown when the PimSystemManager query queue is full
     */
    class PimQueryQueueFullException extends Exception {

        public PimQueryQueueFullException() {
            super("PimSystemManager query queue is full");
        }
    }

    interface ResultReceiver {

        void startResultBatch();

        void addResult(Integer resultId, DataInput result);

        void endResultBatch();
    }
}
