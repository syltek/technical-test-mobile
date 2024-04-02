//
//  DashedBorder.swift
//  PlaytomicUIExample
//
//  Created by Angel Luis Garcia on 16/1/23.
//  Copyright © 2023 Playtomic. All rights reserved.
//

import Foundation
import SwiftUI

public extension View {
    func dashedBorder(lineWidth: CGFloat = 1, color: Color) -> some View {
        overlay(Rectangle().strokeBorder(style: StrokeStyle(lineWidth: lineWidth, dash: [10])).foregroundColor(color))
    }
}
