/*
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { HarnessLoader } from '@angular/cdk/testing';

import { GioChartPieComponentModule } from './gio-chart-pie.component.module';
import { GioChartPieComponent } from './gio-chart-pie.component';
import { GioChartPieHarness } from './gio-chart-pie.harness';

describe('GioChartPieComponent', () => {
  let fixture: ComponentFixture<GioChartPieComponent>;
  let loader: HarnessLoader;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NoopAnimationsModule, GioChartPieComponentModule],
      providers: [],
    });
    fixture = TestBed.createComponent(GioChartPieComponent);
    loader = TestbedHarnessEnvironment.loader(fixture);
  });

  it('should show message when no data', async () => {
    const pieChart = await loader.getHarness(GioChartPieHarness);
    expect(await pieChart.hasNoData()).toBeTruthy();
    expect(await pieChart.displaysChart()).toBeFalsy();
  });
});
