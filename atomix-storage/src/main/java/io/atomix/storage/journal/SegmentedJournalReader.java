/*
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.storage.journal;

import static java.util.Objects.requireNonNull;

import java.util.NoSuchElementException;

/**
 * A {@link JournalReader} traversing all entries.
 */
sealed class SegmentedJournalReader<E> implements JournalReader<E> permits CommitsSegmentJournalReader {
  final SegmentedJournal<E> journal;
  private JournalSegment<E> currentSegment;
  private Indexed<E> previousEntry;
  private JournalSegmentReader<E> currentReader;

  SegmentedJournalReader(SegmentedJournal<E> journal, JournalSegment<E> segment) {
    this.journal = requireNonNull(journal);
    currentSegment = requireNonNull(segment);
    currentReader = segment.createReader();
  }

  @Override
  public final long getFirstIndex() {
    return journal.getFirstSegment().index();
  }

  @Override
  public final long getCurrentIndex() {
    long currentIndex = currentReader.getCurrentIndex();
    if (currentIndex != 0) {
      return currentIndex;
    }
    if (previousEntry != null) {
      return previousEntry.index();
    }
    return 0;
  }

  @Override
  public final Indexed<E> getCurrentEntry() {
    Indexed<E> currentEntry = currentReader.getCurrentEntry();
    if (currentEntry != null) {
      return currentEntry;
    }
    return previousEntry;
  }

  @Override
  public final long getNextIndex() {
    return currentReader.getNextIndex();
  }

  @Override
  public final void reset() {
    previousEntry = null;
    currentReader.close();

    currentSegment = journal.getFirstSegment();
    currentReader = currentSegment.createReader();
  }

  @Override
  public final void reset(long index) {
    // If the current segment is not open, it has been replaced. Reset the segments.
    if (!currentSegment.isOpen()) {
      reset();
    }

    if (index < currentReader.getNextIndex()) {
      rewind(index);
    } else if (index > currentReader.getNextIndex()) {
      forward(index);
    } else {
      currentReader.reset(index);
    }
  }

  /**
   * Rewinds the journal to the given index.
   */
  private void rewind(long index) {
    if (currentSegment.index() >= index) {
      JournalSegment<E> segment = journal.getSegment(index - 1);
      if (segment != null) {
        currentReader.close();

        currentSegment = segment;
        currentReader = currentSegment.createReader();
      }
    }

    currentReader.reset(index);
    previousEntry = currentReader.getCurrentEntry();
  }

  /**
   * Fast forwards the journal to the given index.
   */
  private void forward(long index) {
    while (getNextIndex() < index && hasNext()) {
      next();
    }
  }

  @Override
  public boolean hasNext() {
    return currentReader.hasNext() || moveToNextSegment() && currentReader.hasNext();
  }

  @Override
  public final Indexed<E> next() {
    if (currentReader.hasNext()) {
      previousEntry = currentReader.getCurrentEntry();
      return currentReader.next();
    }
    if (moveToNextSegment()) {
      return currentReader.next();
    }
    throw new NoSuchElementException();
  }

  @Override
  public final void close() {
    currentReader.close();
    journal.closeReader(this);
  }

  private boolean moveToNextSegment() {
    final var nextSegment = journal.getNextSegment(currentSegment.index());
    if (nextSegment == null || nextSegment.index() != getNextIndex()) {
      return false;
    }

    previousEntry = currentReader.getCurrentEntry();
    currentReader.close();

    currentSegment = nextSegment;
    currentReader = currentSegment.createReader();
    return true;
  }
}
