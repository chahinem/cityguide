package com.chahinem.cityguide.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.chahinem.cityguide.ui.MainEvent.LoadMain
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import timber.log.Timber
import javax.inject.Inject

class MainViewModel @Inject constructor(private val interactor: MainInteractor) : ViewModel() {

  internal val data: MutableLiveData<MainModel> = MutableLiveData()
  internal val uiEvents: Subject<MainEvent> = PublishSubject.create()

  private var disposable: Disposable?

  init {
    disposable = uiEvents
        .doOnNext { Timber.d("--> event: ${it.javaClass.simpleName}") }
        .publish { shared ->
          Observable.mergeDelayError(listOf(
              shared
                  .ofType(LoadMain::class.java)
                  .compose(interactor.places())
          ))
        }
        .subscribe(data::postValue, Timber::e)
  }

  override fun onCleared() {
    disposable?.dispose()
  }
}
