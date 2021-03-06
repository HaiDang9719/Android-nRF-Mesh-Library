/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.nrfmeshprovisioner.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import no.nordicsemi.android.nrfmeshprovisioner.di.ViewModelSubComponent;


public class ViewModelFactory implements ViewModelProvider.Factory {
	private final Map<Class, Callable<? extends ViewModel>> creators;

	@Inject
	public ViewModelFactory(final ViewModelSubComponent viewModelSubComponent) {
		creators = new HashMap<>();
		// we cannot inject view models directly because they won't be bound to the owner's
		// view model scope.
		creators.put(SharedViewModel.class, viewModelSubComponent::commonViewModel);
        creators.put(ManageAppKeysViewModel.class, viewModelSubComponent::manageAppKeysViewModel);
		creators.put(MeshProvisionerViewModel.class, viewModelSubComponent::meshProvisionerViewModel);
		creators.put(NodeConfigurationViewModel.class, viewModelSubComponent::meshConfigurationViewModel);
		creators.put(ModelConfigurationViewModel.class, viewModelSubComponent::modelConfigurationViewModel);
		creators.put(ProvisionedNodesScannerViewModel.class, viewModelSubComponent::scannerViewModel);
		creators.put(ReconnectViewModel.class, viewModelSubComponent::reconnectViewModule);
	}

	@NonNull
	@Override
	public <T extends ViewModel> T create(@NonNull final Class<T> modelClass) {
		Callable<? extends ViewModel> creator = creators.get(modelClass);
		if (creator == null) {
			for (Map.Entry<Class, Callable<? extends ViewModel>> entry : creators.entrySet()) {
				if (modelClass.isAssignableFrom(entry.getKey())) {
					creator = entry.getValue();
					break;
				}
			}
		}
		if (creator == null) {
			throw new IllegalArgumentException("unknown model class " + modelClass);
		}
		try {
			return (T) creator.call();
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
