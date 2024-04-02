//
//  MVITypes.swift
//  Mozart
//
//  Created by Angel Luis Garcia on 21/9/21.
//  Copyright © 2021 Playtomic. All rights reserved.
//

import Foundation

public protocol ActionResult { }

public protocol ViewAction { }

public protocol ViewState { }

public class NoViewState: ViewState {
    public static var void = NoViewState()
}

public class NoViewAction: ViewAction { }
public class NoActionResult: ActionResult { }
